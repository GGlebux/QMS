package most.qms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import most.qms.dtos.requests.SendCodeRequest;
import most.qms.dtos.requests.VerifyCodeRequest;
import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/verification")
@Tag(name = "Верификация", description = "Управление верификацией пользователей")
public class VerificationController {
    private final UserService service;

    @Autowired
    public VerificationController(UserService service) {
        this.service = service;
    }

    @PostMapping("/get-code")
    @Operation(summary = "Отправить код",
            description = "Отправляет код на введенный номер",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<?> sendCode(@RequestBody @Valid SendCodeRequest dto) {
        service.sendCodeToUserPhone(dto);
        return ok("Code has been sent");
    }

    @PostMapping("/verify-code")
    @Operation(summary = "Подтвердить код",
            description = "Подтверждает код",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<?> verifyCode(@RequestBody @Valid VerifyCodeRequest dto) {
        service.verifyUserCode(dto);
        return ok("Code has been verified");
    }
}
