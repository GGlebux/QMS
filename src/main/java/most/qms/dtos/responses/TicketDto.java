package most.qms.dtos.responses;

import most.qms.models.TicketStatus;

import java.time.LocalDateTime;

public abstract class TicketDto {
    private TicketStatus status;
    private LocalDateTime createdAt;
}
