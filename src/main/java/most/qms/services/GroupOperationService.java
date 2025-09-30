package most.qms.services;

import most.qms.interfaces.GroupCrud;
import most.qms.interfaces.GroupOperation;
import most.qms.models.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupOperationService implements GroupOperation {
    private final GroupCrud groupCrud;
    private final TicketService ticketService;


    @Autowired
    public GroupOperationService(GroupCrud groupCrud,
                                 TicketService ticketService) {
        this.groupCrud = groupCrud;
        this.ticketService = ticketService;
    }

    @Override
    public void callGroup(Group group) {
        ticketService.callTickets(group.getTickets());
        group.call();
        groupCrud.save(group);
    }

    @Override
    public void completeGroup(Group group) {
        ticketService.completeTickets(group.getTickets());
        group.complete();
        groupCrud.save(group);
    }
}
