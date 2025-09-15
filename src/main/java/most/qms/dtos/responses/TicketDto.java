package most.qms.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import most.qms.models.TicketStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private Long queuePosition;
    private TicketStatus status;
    private LocalDateTime createdAt;
}
