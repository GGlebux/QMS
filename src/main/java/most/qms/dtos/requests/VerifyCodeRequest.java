package most.qms.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Подтверждение номера с кодом")
public class VerifyCodeRequest {
    @NotNull(message = "phoneNumber should not be empty!")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "phoneNumber is incorrect")
    @Schema(description = "Номер телефона (начинается с '+')",
            requiredMode = REQUIRED,
            example = "+79939453152")
    private String phoneNumber;

    @NotNull(message = "code should not be empty!")
    @Size(min = 6, max = 6, message = "code size should be 6")
    @Schema(description = "Код подтверждения (6 символов)",
            requiredMode = REQUIRED,
            example = "597264")
    public String code;
}
