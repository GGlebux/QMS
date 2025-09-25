package most.qms.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import most.qms.models.Role;
import most.qms.models.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Пользователь")
public class UserResponse {
    @Schema(description = "Имя пользователя",
            example = "GGlebux")
    private String name;

    @JsonFormat(pattern = "dd.MM.yyyy")
    @Schema(description = "Дата рождения в формате 'dd.MM.yyyy'",
            example = "16.08.2006")
    private LocalDate birthDate;

    @Schema(description = "Номер телефона (начинается с '+')",
            example = "+79939453152")
    private String phoneNumber;

    @Schema(description = "Верифицирован ли пользователь?",
            example = "false")
    private Boolean isPhoneVerified;

    @Schema(description = "Статус пользователя",
            example = "PENDING")
    private UserStatus status;

    @Schema(description = "Роль пользователя",
            example = "ROLE_USER")
    private Role role;

    @Schema(description = "Время создания",
            example = "2025-09-25T17:10:18.016458556")
    private LocalDateTime createdAt;
}
