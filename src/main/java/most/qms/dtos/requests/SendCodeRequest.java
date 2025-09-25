package most.qms.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Номер телефона на который отправится код")
public class SendCodeRequest {
    @NotNull(message = "phoneNumber should not be empty!")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "phoneNumber is incorrect")
    @Schema(description = "Номер телефона (начинается с '+')",
            requiredMode = REQUIRED,
            example = "+79939453152")
    private String phoneNumber;
}
