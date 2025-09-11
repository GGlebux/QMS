package most.qms.services;

public interface SmsSender {
    default void sendSms(String phoneNumber, String message){}
}
