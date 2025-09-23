package most.qms;

import most.qms.services.SmsSender;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static java.lang.System.out;

@SpringBootApplication
public class QmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(QmsApplication.class, args);
    }

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    @Bean
    public SmsSender smsSender() {
        return new SmsSender() {
            @Override
            public void sendSms(String phoneNumber, String message) {
                out.printf("Send sms {%s} to number {%s}\n", message, phoneNumber);
            }
        };
    }



}
