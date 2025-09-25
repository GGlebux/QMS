package most.qms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import most.qms.dtos.requests.LoginRequest;
import most.qms.dtos.requests.UserRequest;
import most.qms.dtos.responses.JwtAuthResponse;
import most.qms.dtos.responses.UserResponse;
import most.qms.services.AuthService;
import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Аутентификация", description = "Управление регистрацией и авторизацией пользователей")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/auth/sign-up")
    @Operation(summary = "Регистрация пользователя",
            description = "Создает временного пользователя и отправляет на его номер телефона код верификации",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest dto) {
        return userService.create(dto);
    }

    @PostMapping("/auth/sign-in")
    @Operation(summary = "Авторизация пользователя",
            description = "Авторизует пользователя и выдает JWT токен",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginRequest login) {
        return authService.login(login);
    }
}
