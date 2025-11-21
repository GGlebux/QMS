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
@Schema(description = "New password + token")
public class NewPass {
    @NotNull(message = "token should not be empty!")
    @Schema(description = "Access token from SMS",
            requiredMode = REQUIRED,
            example = "032fc9eb-1377-4871-9aa2-2df2490e4bc8")
    private String token;

    @NotNull(message = "newPassword should not be empty!")
    @Size(min = 8, max = 35, message = "newPassword should be in range from 8 to 35 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
            message = "newPassword should contains upper and lower case, special symbols, numbers without spaces")
    @Schema(description = "Password (8-35 characters, different case, special characters and numbers)",
            requiredMode = REQUIRED,
            example = "SuperPass123@")
    private String newPassword;
}
