package most.qms.interfaces;

public interface SmsSender {
    void sendSms(String phoneNumber, String message);
}
