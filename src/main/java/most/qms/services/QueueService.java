package most.qms.services;

import most.qms.AppConfig;
import most.qms.models.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static most.qms.models.GroupStatus.CALLED;
import static most.qms.models.GroupStatus.COMPLETE;

@Service
@Transactional(readOnly = true)
public class QueueService {
    private static final Logger log = LoggerFactory.getLogger(QueueService.class);
    private final AppConfig config;
    private final GroupService groupService;
    private final TicketService ticketService;

    @Autowired
    public QueueService(AppConfig config, GroupService groupService, TicketService ticketService) {
        this.config = config;
        this.groupService = groupService;
        this.ticketService = ticketService;
    }

    @Scheduled(cron = "0 * 7-23 * * *", zone = "Europe/Tallinn")
    @Transactional
    public void autoCallNextGroup() {
        Optional<Group> lastCalled = groupService.findLastCalled();
        // Если timeout группы истёк -> вызываем следующую
        if (lastCalled.isPresent()) {
            var group = lastCalled.get();
            var now = now();
            if (MINUTES.between(group.getCalledAt(), now) >= config.getGroupTimeOut()) {
                this.callNextGroup(lastCalled);
            }
        } else {
            // Если нет вызванной, то ищем ожидающую
            this.callNextGroup(empty());
        }
    }


    @Transactional
    public Optional<Group> callNextGroup(Optional<Group> source) {
        Set<Group> groupSet = new HashSet<>();

        // Обрабатываем последнюю вызванную
        var lastCalled = source.or(groupService::findLastCalled);
        lastCalled.ifPresent(group -> {
            groupSet.add(this.completeGroup(group));
            log.info("Group {} has been successfully completed", group.getName());
        });

        // Ищем следующую для вызова
        Optional<Group> nextGroup = groupService.findNextForCalling();
        if (nextGroup.isEmpty()) {
            log.info("No group found for calling");
        } else if (nextGroup.get().getTickets().isEmpty()) {
            log.info("Group {} is empty", nextGroup.get().getName());
        } else {
            var nextToCalling = this.callGroup(nextGroup.get());
            groupSet.add(nextToCalling);
            log.info("Called next group {}", nextToCalling.getName());
        }

        List<Group> saved = groupService
                .saveAll(groupSet);
        return saved.size() < 2 ? empty() : of(saved.getLast());
    }

    @Transactional
    public Group completeGroup(Group group) {
        group.setCompletedAt(now());
        group.setStatus(COMPLETE);
        ticketService.markAllAsCompletedInGroup(group);
        return group;
    }

    @Transactional
    public Group callGroup(Group group) {
        group.setCalledAt(now());
        group.setStatus(CALLED);
        ticketService.markAllAsCalledInGroup(group);
        return group;
    }
}
