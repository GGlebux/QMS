package most.qms.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import most.qms.models.Ticket;
import most.qms.models.TicketStatus;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Ticket (in a websocket)")
public class UpdatedTicketDto extends TicketDto{
    @Schema(description = "Position in the queue (being updated)",
            example = "12")
    private Long queuePosition;

    @Schema(description = "Approximate waiting time (in minutes)",
            example = "150")
    private Long waitingTime;

    @Schema(description = "Ticket status",
            example = "WAITING")
    private TicketStatus status;
    @Schema(description = "Creation time",
            example = "2025-09-25T17:10:18.016458556")
    private LocalDateTime createdAt;

    public static UpdatedTicketDto from(Ticket entity, Long position, Duration duration) {
        return new UpdatedTicketDto(
                position,
                duration.toMinutes(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
