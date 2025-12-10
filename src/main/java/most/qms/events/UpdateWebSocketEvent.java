package most.qms.events;

import lombok.Getter;
import most.qms.dtos.responses.TicketDto;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateWebSocketEvent extends ApplicationEvent {
    private final String username;
    private final TicketDto ticket;

    public UpdateWebSocketEvent(Object source, String username, TicketDto ticket) {
        super(source);
        this.username = username;
        this.ticket = ticket;
    }
}
