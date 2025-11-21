package most.qms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Getter
public class AppConfig {
    @Value("${group.capacity}")
    private long groupCapacity;

    @Value("${group.timeout}")
    private long groupTimeOut;

    @Value("${group.confirm.percent}")
    private double groupConfirmPercent;

    @Value("${app.front-url}")
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
        return (_, response, _, ex) -> {
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
