package most.qms.controllers;

import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/users/{userId}/verification")
public class PhoneVerificationController {
    private final UserService service;

    @Autowired
    public PhoneVerificationController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> sendCode(@PathVariable Long userId) {
        service.sendCodeToUserPhone(userId);
        return ok("Code has been sent");
    }

    @PostMapping
    public ResponseEntity<?> verifyCode(@PathVariable Long userId,
                                        @RequestBody String code) {
        service.verifyUserCode(userId, code);
        return ok("Code has been verified");
    }
}
