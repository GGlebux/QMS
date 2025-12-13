package most.qms.config;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    private long groupCapacity;
    private long groupTimeout;
    private double groupConfirmPercent;
    private String frontUrl;

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }
}
