package most.qms.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import most.qms.models.Role;
import most.qms.models.UserStatus;
import most.qms.services.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthDate;
    private String phoneNumber;
    private Boolean isPhoneVerified;
    private UserStatus status;
    private Role role;
    private LocalDateTime createdAt;
}
