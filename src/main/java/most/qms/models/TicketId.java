package most.qms.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class TicketId implements Serializable {
    private Long userId;
    private Long groupId;
}
