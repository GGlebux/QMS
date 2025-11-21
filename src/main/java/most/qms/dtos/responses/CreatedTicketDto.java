package most.qms.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import most.qms.models.Ticket;
import most.qms.models.TicketStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Ticket (when creating and canceling)")
public class CreatedTicketDto {
    @Schema(description = "Ticket status",
            example = "WAITING")
    private TicketStatus status;
    @Schema(description = "Creation time",
            example = "2025-09-25T17:10:18.016458556")
    private LocalDateTime createdAt;

    public static CreatedTicketDto from(Ticket entity) {
        return new CreatedTicketDto(
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
