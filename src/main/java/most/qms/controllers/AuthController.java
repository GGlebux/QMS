package most.qms.controllers;

import most.qms.dtos.requests.LoginRequest;
import most.qms.dtos.requests.UserRequest;
import most.qms.dtos.responses.UserResponse;
import most.qms.services.SMSSenderService;
import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final SMSSenderService smsSenderService;

    @Autowired
    public AuthController(UserService userService, SMSSenderService smsSenderService) {
        this.userService = userService;
        this.smsSenderService= smsSenderService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long userId) {
        return userService.findDtoById(userId);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest dto) {
        return userService.create(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        smsSenderService.SendSMS("+37258087319","hey you, lol");
        return status(FORBIDDEN).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LoginRequest logout) {
        return status(FORBIDDEN).build();
    }
}
