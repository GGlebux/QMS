package most.qms.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import most.qms.models.TicketStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Билет")
public class TicketDto {
    @Schema(description = "Позиция билета в очереди",
            example = "15")
    private Long queuePosition;
    @Schema(description = "Статус билета",
            example = "WAITING")
    private TicketStatus status;
    @Schema(description = "Время создания",
            example = "2025-09-25T17:10:18.016458556")
    private LocalDateTime createdAt;
}
