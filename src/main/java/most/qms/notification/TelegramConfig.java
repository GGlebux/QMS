package most.qms.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {
    private final MyTelegramBot myTelegramBot;

    @Autowired
    public TelegramConfig(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    @Bean
    public TelegramBotsApi telegramBot() throws Exception{
        var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myTelegramBot);
        return botsApi;
    }
}
