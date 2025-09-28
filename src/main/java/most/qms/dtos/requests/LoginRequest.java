package most.qms.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@AllArgsConstructor
@Schema(description = "Данные для логина")
public class LoginRequest {
    @NotNull(message = "phoneNumber should not be empty!")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "phoneNumber is incorrect")
    @Schema(description = "Номер телефона (начинается с '+')",
            requiredMode = REQUIRED,
            example = "+79939453152")
    private String phoneNumber;

    @NotNull(message = "password should not be empty!")
    @Size(min = 8, max = 35, message = "password should be in range from 8 to 35 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
            message = "password should contains upper and lower case, special symbols, numbers without spaces")
    @Schema(description = "Пароль (8-35 символов, разный регистр, спец символы и числа)",
            requiredMode = REQUIRED,
            example = "SuperPass123@")
    private String password;
}
