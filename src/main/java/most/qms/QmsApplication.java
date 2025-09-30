package most.qms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;

@SpringBootApplication
public class QmsApplication {
    public static void main(String[] args) {
        setDefault(getTimeZone("Europe/Tallinn"));
        SpringApplication.run(QmsApplication.class, args);
    }
}
