package most.qms.services;

import most.qms.config.AppConfig;
import most.qms.events.NextGroupEvent;
import most.qms.interfaces.GroupCrud;
import most.qms.interfaces.PartCompletedQueue;
import most.qms.interfaces.ScheduledQueue;
import most.qms.models.Group;
import most.qms.models.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class AutoCallQueue implements ScheduledQueue, PartCompletedQueue {
    private final GroupCrud groupCrud;
    private final AppConfig config;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public AutoCallQueue(GroupCrud groupCrud,
                         AppConfig config,
                         ApplicationEventPublisher publisher) {
        this.groupCrud = groupCrud;
        this.config = config;
        this.publisher = publisher;
    }


    @Override
    @Scheduled(cron = "0 * 7-23 * * *", zone = "Europe/Tallinn")
    @Transactional
    public void autoCallNextGroup() {
        Optional<Group> lastCalled = groupCrud.findLastCalled();
        var event = new NextGroupEvent(this, lastCalled);
        // Если timeout группы истёк -> вызываем следующую
        if (lastCalled.isPresent()) {
            var group = lastCalled.get();
            var now = now();
            if (MINUTES.between(group.getCalledAt(), now) >= config.getGroupTimeOut()) {
                publisher.publishEvent(event);
            }
        } else {
            // Если нет вызванной, то ищем ожидающую
            publisher.publishEvent(event);
        }
    }

    @Override
    public void callIfPartOfGroupComplete(Group group) {
        long countOfCompleted = group
                .getTickets()
                .stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.COMPLETE)
                .count();
        boolean canCall = (double) config.getGroupCapacity() / countOfCompleted >= config.getGroupConfirmPercent();
        if (canCall) {
            var event = new NextGroupEvent(this, Optional.of(group));
            publisher.publishEvent(event);
        }
    }
}
