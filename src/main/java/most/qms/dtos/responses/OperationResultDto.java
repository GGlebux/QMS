package most.qms.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Schema(description = "Operation Result DTO")
@Builder
public class OperationResultDto {
    @Schema(description = "Operation status",
            example = "SUCCESS")
    private OperationStatus status;

    @Schema(description = "Message (optional)",
            example = "Ticket created successfully")
    private String message;

    @Schema(description = "Map of errors",
            example = "EntityNotFoundException: Ticket with id=10 not found!")
    Map<String, String> errors;


    public enum OperationStatus {
        SUCCESS,
        FAILURE
    }
}
