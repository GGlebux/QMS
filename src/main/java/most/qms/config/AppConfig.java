package most.qms.config;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Getter
public class AppConfig {
    @Value("${group.capacity}")
    private long groupCapacity;

    @Value("${group.timeout}")
    private long groupTimeOut;

    @Value("${group.confirm.percent}")
    private double groupConfirmPercent;

    private final LongPollingBot smsSender;

    @Autowired
    public AppConfig(LongPollingBot smsSender) {
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
}
