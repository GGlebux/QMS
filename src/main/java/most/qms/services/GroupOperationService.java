package most.qms.services;

import most.qms.interfaces.GroupOperation;
import most.qms.models.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupOperationService implements GroupOperation {
    private final GroupCrudService groupCrudService;
    private final TicketService ticketService;


    @Autowired
    public GroupOperationService(GroupCrudService groupCrudService,
                                 TicketService ticketService) {
        this.groupCrudService = groupCrudService;
        this.ticketService = ticketService;
    }

    @Override
    public void callGroup(Group group) {
        ticketService.callTickets(group.getTickets());
        group.call();
        groupCrudService.save(group);
    }

    @Override
    public void completeGroup(Group group) {
        ticketService.completeTickets(group.getTickets());
        group.complete();
        groupCrudService.save(group);
    }
}
