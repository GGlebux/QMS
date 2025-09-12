package most.qms.controllers;

import most.qms.dtos.requests.UserRequest;
import most.qms.models.User;
import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserRequest dto) {
        return ok(service.save(dto));
    }

    @PostMapping("/{userId}/send_code")
    public ResponseEntity<?> sendCode(@PathVariable Long userId) {
        service.sendCodeToUserPhone(userId);
        return ok("Code has been sent");
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> verifyCode(@PathVariable Long userId,
                                        @RequestParam String code) {
        service.verifyCode(userId, code);
        return ok("Code has been verified");
    }
}
