package most.qms.interfaces;

import most.qms.dtos.responses.UpdatedTicketDto;
import most.qms.models.Ticket;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface TicketUpdater {
    void updateOneTicket(Ticket source);

    void updateAllTickets();

    void callTickets(List<Ticket> all);

    Map<Long, Long> calculatePositions(List<Ticket> tickets);

    Map<Long, Duration> calculateDurations(List<Ticket> tickets);

    void publishUpdateSocketEvent(Object sender, String username, UpdatedTicketDto message);
}
