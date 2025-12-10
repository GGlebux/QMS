package most.qms.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import most.qms.models.Ticket;
import most.qms.models.TicketStatus;

import java.time.LocalDateTime;

import static most.qms.models.TicketStatus.COMPLETE;

@Getter
@Setter
@Schema(description = "Ticket (when complete)")
public class CompletedTicketDto extends TicketDto {
    @Schema(description = "Ticket status",
            example = "always COMPLETE")
    private final TicketStatus status = COMPLETE;

    @Schema(description = "Creation time",
            example = "2025-09-25T17:10:18.016458556")
    private LocalDateTime createdAt;

    @Schema(description = "Completed time",
            example = "2025-09-25T19:10:18.016456556")
    private LocalDateTime completedAt;

    public CompletedTicketDto(LocalDateTime createdAt, LocalDateTime completedAt) {
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public static CompletedTicketDto from(Ticket t){
        return new CompletedTicketDto(
                t.getCreatedAt(),
                t.getCompletedAt()
        );
    }
}

