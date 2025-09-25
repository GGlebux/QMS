package most.qms.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyCodeRequest {
    @NotNull(message = "phoneNumber should not be empty!")
    private String phoneNumber;
    @NotNull(message = "code should not be empty!")
    public String code;
}
