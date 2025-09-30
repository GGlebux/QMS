package most.qms.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import most.qms.models.Ticket;
import most.qms.models.TicketStatus;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
@Schema(description = "Билет (в веб-сокете)")
public class UpdatedTicketDto {
    @Schema(description = "Позиция в очереди (обновляемая)",
            example = "12")
    private Long queuePosition;

    @Schema(description = "Примерное время ожидания (в минутах)",
            example = "150")
    private Long waitingTime;

    @Schema(description = "Статус билета",
            example = "WAITING")
    private TicketStatus status;
    @Schema(description = "Время создания",
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
