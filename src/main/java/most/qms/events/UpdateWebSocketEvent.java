package most.qms.events;

import lombok.Getter;
import most.qms.dtos.responses.UpdatedTicketDto;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateWebSocketEvent extends ApplicationEvent {
    private final String username;
    private final UpdatedTicketDto ticket;

    public UpdateWebSocketEvent(Object source, String username, UpdatedTicketDto ticket) {
        super(source);
        this.username = username;
        this.ticket = ticket;
    }
}
