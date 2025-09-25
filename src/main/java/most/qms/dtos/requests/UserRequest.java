package most.qms.dtos.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Данные для регистрации")
public class UserRequest {
    @NotNull(message = "name should not be empty!")
    @Size(min = 2, max = 64, message = "name should be in range from 2 to 64 characters")
    @Schema(description = "Имя пользователя (от 2 до 64 символов)",
            requiredMode = REQUIRED,
            example = "GGlebux")
    private String name;

    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotNull(message = "birthDate should not be empty!")
    @Schema(description = "Дата рождения в формате 'dd.MM.yyyy'",
            requiredMode = REQUIRED,
            example = "16.08.2006")
    private LocalDate birthDate;

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
    @Schema(description = "Пароль (8-35 символов, разный регистр, спец символы и числа, без пробелов)",
            requiredMode = REQUIRED,
            example = "SuperPass123@")
    private String password;
}
