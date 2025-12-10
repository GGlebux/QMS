package most.qms.interfaces;

import most.qms.dtos.responses.TicketDto;
import most.qms.models.Group;
import most.qms.models.Ticket;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface TicketUpdater {
    void updateOneTicket(Ticket source);

    void updateAllTickets();

    void completeAllTicketsInGroup(Group group);

    void callTickets(List<Ticket> all);

    Map<Long, Long> calculatePositions(List<Ticket> tickets);

    Map<Long, Duration> calculateDurations(List<Ticket> tickets);

    void publishUpdateSocketEvent(Object sender, String username, TicketDto message);
}
