package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.AppConfig;
import most.qms.dtos.responses.TicketDto;
import most.qms.events.NextGroupEvent;
import most.qms.exceptions.EntityNotCreatedException;
import most.qms.models.Group;
import most.qms.models.Ticket;
import most.qms.models.TicketStatus;
import most.qms.repositories.TicketRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.time.LocalDateTime.now;
import static java.util.EnumSet.of;
import static most.qms.models.TicketStatus.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Service
@Transactional(readOnly = true)
public class TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final AppConfig config;
    private final TicketRepository repo;
    private final UserService userService;
    private final GroupService groupService;
    private final DailyCounterService dailyCounterService;
    private final ApplicationEventPublisher publisher;
    private static final EnumSet<TicketStatus> ACTIVE_TICKET_STATUSES;

    static {
        ACTIVE_TICKET_STATUSES = of(WAITING, CALLED);
    }

    @Autowired
    TicketService(AppConfig config, TicketRepository repo, UserService userService, GroupService groupService, DailyCounterService dailyCounterService, ApplicationEventPublisher publisher) {
        this.config = config;
        this.repo = repo;
        this.userService = userService;
        this.groupService = groupService;
        this.dailyCounterService = dailyCounterService;
        this.publisher = publisher;
    }

    public List<TicketDto> findAll() {
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    @Transactional
    public ResponseEntity<TicketDto> create() {
        var user = userService.getUserFromContextAndVerify();

        boolean hasActiveTickets = user
                .getTickets()
                .stream()
                .map(Ticket::getStatus)
                .anyMatch(ACTIVE_TICKET_STATUSES::contains);

        if (hasActiveTickets) {
            throw new EntityNotCreatedException(
                    "User with phoneNumber=%s already has active ticket!"
                            .formatted(user.getPhoneNumber()));
        }

        Group group = groupService.getLastAvailable();
        Long number = dailyCounterService.getAndIncrement();

        Ticket saved = repo.save(new Ticket(user, group, number));

        group.getTickets().add(saved);
        groupService.save(group);
        user.getTickets().add(saved);
        userService.save(user);

        return ok(convertToDto(saved));
    }

    @Transactional
    public ResponseEntity<TicketDto> markAsComplete() {
        var user = userService.getUserFromContextAndVerify();
        var ticket = repo
                .findActiveByUserId(user.getId())
                .orElseThrow(throwActiveTicketNotFound());
        ticket.setStatus(COMPLETE);
        ticket.setCompletedAt(now());

        var saved = repo.save(ticket);

        var group = ticket.getGroup();
        if (isPartOfGroupComplete(group)) {
            publisher.publishEvent(new NextGroupEvent(this, Optional.of(group)));
            System.err.println("Publish!!!");
        }

        return ok(convertToDto(saved));
    }


    @Transactional
    public ResponseEntity<String> cancel() {
        var user = userService.getUserFromContextAndVerify();
        Ticket ticket = repo
                .findActiveByUserId(user.getId())
                .orElseThrow(throwActiveTicketNotFound());
        ticket.setStatus(CANCELED);
        ticket.setGroup(null);
        repo.save(ticket);

        Group group = ticket.getGroup();
        group.getTickets().remove(ticket);
        groupService.save(group);
        return status(NO_CONTENT)
                .body("Ticket № %s has been cancelled!"
                        .formatted(ticket.getNumber()));
    }


    @Transactional
    public void markAllAsCalledInGroup(Group group) {
        Set<Ticket> tickets = group.getTickets();
        tickets
                .forEach(ticket -> ticket.setStatus(CALLED));
        repo.saveAll(tickets);
    }

    @Transactional
    public void markAllAsCompletedInGroup(Group group) {
        Set<Ticket> tickets = group.getTickets();
        tickets
                .forEach(ticket -> {
                    ticket.setCompletedAt(now());
                    ticket.setStatus(COMPLETE);
                });
        repo.saveAll(tickets);
    }

    private TicketDto convertToDto(Ticket entity) {
        return new TicketDto(entity.getNumber(),
                entity.getStatus(),
                entity.getCreatedAt());
    }

    private static @NotNull Supplier<EntityNotFoundException> throwActiveTicketNotFound() {
        return () -> new EntityNotFoundException(
                "Active tickets not found!");
    }

    private boolean isPartOfGroupComplete(Group group) {
        long countOfCompleted = group
                .getTickets()
                .stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.COMPLETE)
                .count();
        return (double) config.getGroupCapacity() / countOfCompleted >= config.getGroupConfirmPercent();
    }
}
