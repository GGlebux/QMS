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
@Schema(description = "Registration data")
public class UserRequest {
    @NotNull(message = "name should not be empty!")
    @Size(min = 2, max = 64, message = "name should be in range from 2 to 64 characters")
    @Schema(description = "User name (from 2 to 64 characters)",
            requiredMode = REQUIRED,
            example = "GGlebux")
    private String name;

    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotNull(message = "birthDate should not be empty!")
    @Schema(description = "Date of birth in the format 'dd.MM.yyyy'",
            requiredMode = REQUIRED,
            example = "16.08.2006")
    private LocalDate birthDate;

    @NotNull(message = "phoneNumber should not be empty!")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "phoneNumber is incorrect")
    @Schema(description = "Phone number (starts with '+')",
            requiredMode = REQUIRED,
            example = "+79939453152")
    private String phoneNumber;

    @NotNull(message = "password should not be empty!")
    @Size(min = 8, max = 35, message = "password should be in range from 8 to 35 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
            message = "password should contains upper and lower case, special symbols, numbers without spaces")
    @Schema(description = "Password (8-35 characters, different case, special characters and numbers, without spaces)",
            requiredMode = REQUIRED,
            example = "SuperPass123@")
    private String password;
}
