package most.qms;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {
    @Value("${group.capacity}")
    private long groupCapacity;

    @Value("${group.timeout}")
    private long groupTimeOut;

    @Value("${group.confirm.percent}")
    private double groupConfirmPercent;
}
