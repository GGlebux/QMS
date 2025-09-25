package most.qms.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendCodeRequest {
    @NotNull(message = "userId should not be empty!")
    private String phoneNumber;
}
