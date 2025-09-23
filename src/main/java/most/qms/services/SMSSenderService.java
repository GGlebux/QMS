package most.qms.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;

public class SMSSenderService {
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    public void SendSMS( String number, String msg){
        Twilio.init(accountSid,authToken);
        Message message = Message.creator(new PhoneNumber(number),new PhoneNumber("+18102558787"),msg).create();
        var Body = message.getBody();
    }
}
