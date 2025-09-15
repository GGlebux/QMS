package most.qms.dtos.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {
    @NotNull(message = "name should not be empty!")
    @Size(min = 2, max = 64, message = "name should be in range from 2 to 64 characters")
    private String name;

    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotNull(message = "birthDate should not be empty!")
    private LocalDate birthDate;

    @NotNull(message = "phoneNumber should not be empty!")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "phoneNumber is incorrect")
    private String phoneNumber;

    @NotNull(message = "password should not be empty!")
    @Size(min = 8, max = 35, message = "password should be in range from 8 to 35 characters")
    @Pattern(regexp = "/^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,35}$/",
            message = "password should contains upper and lower case, special symbols, numbers")
    private String password;
}
