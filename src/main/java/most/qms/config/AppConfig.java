package most.qms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

import static java.util.Map.of;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    private long groupCapacity;
    private long groupTimeout;
    private double groupConfirmPercent;
    private String frontUrl;

    private final LongPollingBot smsSender;

    @Autowired
    public AppConfig( LongPollingBot smsSender) {
        this.smsSender = smsSender;
    }


    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    @Bean
    public TelegramBotsApi telegramBot() throws Exception {
        var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(smsSender);
        return botsApi;
    }

    @Bean
    public HandlerExceptionResolver noHandlerFoundResolver() {
        return (req, response, obj, ex) -> {
            if (ex instanceof NoHandlerFoundException) {
                response.setStatus(NOT_FOUND.value());
                response.setContentType("application/json");
                try {
                    response.getWriter().write(new ObjectMapper().writeValueAsString(
                            of("error", "NotFound", "message", "Resource not found")
                    ));
                } catch (IOException ignored) {}
                return new ModelAndView();
            }
            return null;
        };
    }
}
