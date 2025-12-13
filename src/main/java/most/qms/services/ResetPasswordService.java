package most.qms.services;

import jakarta.transaction.Transactional;
import most.qms.config.AppConfig;
import most.qms.dtos.responses.OperationResultDto;
import most.qms.exceptions.EntityNotFoundException;
import most.qms.exceptions.VerificationException;
import most.qms.interfaces.SmsSender;
import most.qms.models.ResetPassword;
import most.qms.models.User;
import most.qms.repositories.ResetPasswordRepository;
import most.qms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static most.qms.dtos.responses.OperationResultDto.OperationStatus.SUCCESS;
import static org.springframework.http.ResponseEntity.ok;

@Service
public class ResetPasswordService {
    private final ResetPasswordRepository repo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final SmsSender smsSender;
    private final AppConfig appConfig;
    private final String SMS_TEMPLATE;

    @Autowired
    public ResetPasswordService(ResetPasswordRepository repo,
                                UserRepository userRepo,
                                PasswordEncoder encoder,
                                SmsSender smsSender,
                                AppConfig appConfig) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.smsSender = smsSender;
        this.appConfig = appConfig;
        SMS_TEMPLATE = "Password reset link: %s/reset?token=%s\n";
    }

    @Transactional
    public ResponseEntity<OperationResultDto> requestPasswordReset(String phoneNumber) {
        User user = userRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        repo.deleteAllByUser(user);
        repo.flush();

        String token = randomUUID().toString();
        ResetPassword resetToken = new ResetPassword();

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(now().plusMinutes(30));
        repo.save(resetToken);

        smsSender.sendSms(user.getPhoneNumber(), SMS_TEMPLATE.formatted(appConfig.getFrontUrl(), token));
        return ok(OperationResultDto.builder()
                .status(SUCCESS)
                .message("Reset link sent!")
                .build());
    }

    @Transactional
    public ResponseEntity<OperationResultDto> resetPassword(String token, String newPassword) {
        ResetPassword resetToken = repo.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid token!"));

        if (resetToken.getExpiryDate().isBefore(now())) {
            throw new VerificationException("Token expired!");
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);

        repo.delete(resetToken);
        return ok(OperationResultDto.builder()
                .status(SUCCESS)
                .message("Password changed!")
                .build());
    }
}
