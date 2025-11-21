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
@Tag(name = "Authentication", description = "User registration and authorization management")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/auth/sign-up")
    @Operation(summary = "User Registration",
            description = "Creates a temporary user and sends the verification code to their phone number",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest dto) {
        return userService.create(dto);
    }

    @PostMapping("/auth/sign-in")
    @Operation(summary = "User authorization",
            description = "Authorizes the user and issues a JWT token",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginRequest login) {
        return authService.login(login);
    }
}
