package most.qms.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "The phone number to which the code will be sent")
public class PhoneNumber {
    @NotNull(message = "phoneNumber should not be empty!")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "phoneNumber is incorrect")
    @Schema(description = "Phone number (starts with '+')",
            requiredMode = REQUIRED,
            example = "+79939453152")
    private String number;
}
