package most.qms.controllers;

import most.qms.dtos.requests.SendCodeRequest;
import most.qms.dtos.requests.VerifyCodeRequest;
import most.qms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/verification")
public class PhoneVerificationController {
    private final UserService service;

    @Autowired
    public PhoneVerificationController(UserService service) {
        this.service = service;
    }

    @PostMapping("/get-code")
    public ResponseEntity<?> sendCode(@RequestBody SendCodeRequest dto) {
        service.sendCodeToUserPhone(dto);
        return ok("Code has been sent");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest dto) {
        service.verifyUserCode(dto);
        return ok("Code has been verified");
    }
}
