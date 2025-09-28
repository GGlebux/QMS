package most.qms.events;

import lombok.Getter;
import most.qms.models.Group;
import org.springframework.context.ApplicationEvent;

import java.util.Optional;

public class NextGroupEvent extends ApplicationEvent {
    @Getter
    private final Optional<Group> group;

    public NextGroupEvent(Object source, Optional<Group> group) {
        super(source);
        this.group = group;
    }
}
