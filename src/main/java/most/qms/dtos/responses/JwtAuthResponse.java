package most.qms.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "JWT токен аутентификации")
public class JwtAuthResponse {
    @Schema(description = "JWT токен",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiKzc5OTM5NDUzMTUyIiwiaWF0IjoxNzU4Nzk3OTMyLCJleHAiOjE3NTg4ODQzMzJ9.aBRIj0SvZkMTOQjdu6qRZ4BsiFevVhTkUXXZ834mJFY")
    private String token;
}
