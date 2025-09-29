package most.qms.events;

import lombok.Getter;
import most.qms.dtos.responses.UpdatedTicketDto;
import org.springframework.context.ApplicationEvent;

public class UpdateQueueEvent extends ApplicationEvent {
    @Getter
    private final UpdatedTicketDto ticket;

    public UpdateQueueEvent(Object source, UpdatedTicketDto ticket) {
        super(source);
        this.ticket = ticket;
    }
}
