package most.qms.services;

import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;

import static com.twilio.Twilio.init;
import static com.twilio.rest.api.v2010.account.Message.creator;

public class SMSSenderService implements SmsSender {
    
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Override
    public void sendSms(String phoneNumber, String message) {
        init(accountSid, authToken);
        var msg = creator(new PhoneNumber(phoneNumber), new PhoneNumber("+18102558787"), message).create();
        var Body = msg.getBody();
    }
}
