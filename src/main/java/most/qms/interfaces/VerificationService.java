package most.qms.interfaces;

public interface VerificationService {
    void sendVerificationCode(String recipient);

    void verifyCode(String recipient, String codeFromUser);

    String generateCode();
}
