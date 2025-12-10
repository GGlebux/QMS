package most.qms.services;

import com.google.common.collect.TreeMultiset;
import most.qms.config.AppConfig;
import most.qms.dtos.responses.CompletedTicketDto;
import most.qms.dtos.responses.TicketDto;
import most.qms.dtos.responses.UpdatedTicketDto;
import most.qms.events.UpdateWebSocketEvent;
import most.qms.exceptions.TicketNotUpdateException;
import most.qms.interfaces.TicketUpdater;
import most.qms.models.Group;
import most.qms.models.Ticket;
import most.qms.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.BoundType.OPEN;
import static com.google.common.collect.TreeMultiset.create;
import static java.time.Duration.ofMinutes;
import static java.util.Comparator.comparing;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static most.qms.dtos.responses.UpdatedTicketDto.from;

@Service
public class TicketUpdaterService implements TicketUpdater {
    private static final Logger log = LoggerFactory.getLogger(TicketUpdaterService.class);
    private final ApplicationEventPublisher publisher;
    private final AppConfig config;
    private final TicketRepository repo;

    @Autowired
    public TicketUpdaterService(ApplicationEventPublisher publisher, AppConfig config, TicketRepository repo) {
        this.publisher = publisher;
        this.config = config;
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public void updateOneTicket(Ticket source) {
        List<Ticket> all = repo.findAllWaiting();
        var ticketForMessage = createUpdatedTicket(source, all);
        publishUpdateSocketEvent(this, source.getUser().getUsername(), ticketForMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public void updateAllTickets() {
        List<Ticket> all = repo.findAllWaiting();
        List<UpdatedTicketDto> allToUpdate = createUpdatedTickets(all);
        if (all.size() != allToUpdate.size()) {
            String msg = "Source tickets size=%d and to update ticket size=%d"
                    .formatted(all.size(), allToUpdate.size());
            log.error(msg);
            throw new TicketNotUpdateException(msg);
        }
        int counter = 0;
        for (Ticket ticket : all) {
            publishUpdateSocketEvent(this, ticket.getUser().getUsername(), allToUpdate.get(counter));
            counter++;
        }
    }

    @Override
    public void completeAllTicketsInGroup(Group group) {
        Set<Ticket> allCompleted = group.getTickets();
        for (Ticket ticket : allCompleted) {
            var completed = CompletedTicketDto.from(ticket);
            publishUpdateSocketEvent(this, ticket.getUser().getUsername(), completed);
        }
    }

    @Override
    public void callTickets(List<Ticket> all) {
        for (Ticket ticket : all) {
            var msg = from(ticket, 0L, ofMinutes(0));
            publishUpdateSocketEvent(this, ticket.getUser().getUsername(), msg);
        }
    }

    private UpdatedTicketDto createUpdatedTicket(Ticket source, List<Ticket> all) {
        Long sourceId = source.getId();
        Long position = this.calculatePositions(all)
                .get(sourceId);
        Duration duration = this.calculateDurations(all)
                .get(sourceId);

        return from(source, position, duration);
    }

    private List<UpdatedTicketDto> createUpdatedTickets(List<Ticket> all) {
        List<UpdatedTicketDto> updatedTickets = new ArrayList<>();
        var positions = this.calculatePositions(all);
        var durations = this.calculateDurations(all);

        for (Ticket source : all) {
            Long id = source.getId();
            var temp = from(source, positions.get(id), durations.get(id));
            updatedTickets.add(temp);
        }
        return updatedTickets;
    }

    @Override
    public void publishUpdateSocketEvent(Object sender, String username, TicketDto message) {
        var event = new UpdateWebSocketEvent(sender, username, message);
        publisher.publishEvent(event);
    }

    @Override
    public Map<Long, Long> calculatePositions(List<Ticket> tickets) {
        var sortedIds = tickets
                .stream()
                .sorted()                  // По дате создания
                .map(Ticket::getId)        // Оставляем только id
                .toList();

        return range(0, sortedIds.size())
                .mapToObj(i -> entry(sortedIds.get(i), (long) i + 1))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<Long, Duration> calculateDurations(List<Ticket> tickets) {
        // Посчитать сколько групп перед билетом и умножить на константу

        // Все ожидающие группы
        Set<Group> groups = tickets
                .stream()
                .map(Ticket::getGroup)
                .collect(toSet());

        // Спец. коллекция для получения групп выше по рангу
        TreeMultiset<Group> multiGroups = create(comparing(Group::getCreatedAt));
        multiGroups.addAll(groups);

        return tickets
                .stream()
                .collect(toMap(Ticket::getId, t -> toDuration(countAhead(t.getGroup(), multiGroups))));
    }

    private static Long countAhead(Group group, TreeMultiset<Group> set) {
        return (long) set.headMultiset(group, OPEN).size();
    }

    private Duration toDuration(Long groupsAhead) {
        return ofMinutes((groupsAhead + 1) * config.getGroupTimeout());
    }
}
