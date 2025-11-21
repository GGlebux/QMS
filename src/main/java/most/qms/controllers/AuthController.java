package most.qms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import most.qms.dtos.requests.LoginRequest;
import most.qms.dtos.requests.NewPass;
import most.qms.dtos.requests.PhoneNumber;
import most.qms.dtos.requests.UserRequest;
import most.qms.dtos.responses.JwtAuthResponse;
import most.qms.dtos.responses.UserResponse;
import most.qms.exceptions.EntityNotFoundException;
import most.qms.exceptions.VerificationException;
import most.qms.services.AuthService;
import most.qms.services.ResetPasswordService;
import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and authorization management")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final ResetPasswordService resetPasswordService;

    @Autowired
    public AuthController(UserService userService,
                          AuthService authService,
                          ResetPasswordService resetPasswordService) {
        this.userService = userService;
        this.authService = authService;
        this.resetPasswordService = resetPasswordService;
    }

    @PostMapping("/sign-up")
    @Operation(summary = "User Registration",
            description = "Creates a temporary user and sends the verification code to their phone number",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest dto) {
        return userService.create(dto);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "User authorization",
            description = "Authorizes the user and issues a JWT token",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginRequest login) {
        return authService.login(login);
    }

    @PostMapping("/request-reset")
    @Operation(summary = "Password Reset request",
            description = "Sends a link to reset the password to the phone number",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<String> requestReset(@RequestBody PhoneNumber body) {
        try {
            return resetPasswordService.requestPasswordReset(body.getNumber());
        } catch (EntityNotFoundException e) {
        return status(NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/reset")
    @Operation(summary = "Password Reset",
            description = "Sets a new password for the token",
            security = @SecurityRequirement(name = "noAuth"))
    public ResponseEntity<String> reset(@RequestBody NewPass body) {
        try {
            return resetPasswordService.resetPassword(body.getToken(), body.getNewPassword());
        } catch (EntityNotFoundException e){
            return status(NOT_FOUND).body(e.getMessage());
        } catch (VerificationException e){
            return status(BAD_REQUEST).body(e.getMessage());
        }
    }
}
