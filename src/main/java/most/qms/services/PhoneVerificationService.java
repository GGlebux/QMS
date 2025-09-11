package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.exceptions.VerificationException;
import most.qms.models.PhoneVerification;
import most.qms.repositories.PhoneVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.valueOf;
import static java.util.concurrent.ThreadLocalRandom.current;

@Service
@Transactional(readOnly = true)
public class PhoneVerificationService {
    private final PhoneVerificationRepository repo;
    private final SmsSender sender;
    private static final String SMS_TEMPLATE = """
            Hello from MOST.\
            
            Your verification code is: %s.\
            
            The code will expire in 5 minutes""";

    @Autowired
    public PhoneVerificationService(PhoneVerificationRepository repo, SmsSender sender) {
        this.repo = repo;
        this.sender = sender;
    }

    @Transactional
    public void sendVerificationCode(String phoneNumber) {
        repo.deleteByPhoneNumber(phoneNumber);

        String code = this.generateCode();

        PhoneVerification phoneVerification = new PhoneVerification(phoneNumber, code);
        repo.save(phoneVerification);

        sender.sendSms(phoneNumber, SMS_TEMPLATE.formatted(code));
    }

    public Boolean verifyCode(String phoneNumber, String codeFromUser) {
        PhoneVerification verification = repo
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Phone number %s not found!".formatted(phoneNumber)));
        if (!verification.getCode().equals(codeFromUser)) {
            throw new VerificationException("Invalid verification code!");
        }
        if (verification.isExpired()) {
            throw new VerificationException("Verification expired!");
        }

        return true;
    }

    private String generateCode() {
        return valueOf(current().nextInt(100_000, 999_999));
    }
}
