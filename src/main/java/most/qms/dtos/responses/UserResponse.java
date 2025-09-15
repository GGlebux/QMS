package most.qms.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import most.qms.models.Role;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private Boolean isPhoneVerified;
    private Role role;
    private LocalDateTime createdAt;
}
