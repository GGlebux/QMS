package most.qms.services;

public interface SmsSender {
    void sendSms(String phoneNumber, String message);
}
