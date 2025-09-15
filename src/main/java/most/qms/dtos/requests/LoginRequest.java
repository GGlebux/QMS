package most.qms.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "phoneNumber should not be empty!")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "phoneNumber is incorrect")
    private String phoneNumber;

    @NotNull(message = "password should not be empty!")
    @Size(min = 8, max = 35, message = "password should be in range from 8 to 35 characters")
    @Pattern(regexp = "/^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,35}$/",
            message = "password should contains upper and lower case, special symbols, numbers")
    private String password;
}
