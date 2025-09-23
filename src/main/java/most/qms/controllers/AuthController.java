package most.qms.controllers;

import most.qms.dtos.requests.LoginRequest;
import most.qms.dtos.requests.UserRequest;
import most.qms.dtos.responses.UserResponse;
import most.qms.services.AuthService;
import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long userId) {
        return userService.findDtoById(userId);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest dto) {
        return userService.create(dto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        return authService.login(login);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody LoginRequest logout) {
        return status(FORBIDDEN).build();
    }
}
