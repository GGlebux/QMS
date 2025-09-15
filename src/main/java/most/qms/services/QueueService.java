package most.qms.services;

import most.qms.models.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDateTime.now;
import static java.util.Set.of;
import static most.qms.models.GroupStatus.COMPLETE;

@Service
@Transactional(readOnly = true)
public class QueueService {
    private final GroupService groupService;
    private final TicketService ticketService;

    @Autowired
    public QueueService(GroupService groupService, TicketService ticketService) {
        this.groupService = groupService;
        this.ticketService = ticketService;
    }

    // ToDo: Call this method if 50%+ tickets press 'I am complete' or if timeout (maybe 30 min for group)
    @Transactional
    public Group callNextGroup() {
        Group previousGroup = groupService.findLastCalled();
        previousGroup.setCompletedAt(now());
        previousGroup.setStatus(COMPLETE);
        ticketService.markAllAsCompletedInGroup(previousGroup);

        Group nextGroup = groupService.findNextForCalling();
        nextGroup.setCalledAt(now());
        ticketService.markAllAsWaitingInGroup(nextGroup);

        return groupService
                .saveAll(of(previousGroup, nextGroup));
    }

    @Transactional
    public void moveGroupToNextDay(Group group) {
        // ToDo
    }
}
