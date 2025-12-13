package most.qms.notification;

import most.qms.config.TelegramConfig;
import most.qms.interfaces.SmsSender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Deprecated
public class TelegramSender implements SmsSender {
    private static final Log log = LogFactory.getLog(TelegramSender.class);
    private final TelegramConfig config;
    private final AbsSender sender;

    @Autowired
    public TelegramSender(TelegramConfig config, AbsSender sender) {
        this.config = config;
        this.sender = sender;
    }

    @Override
    public void sendSms(String phoneNumber, String message)  {
        String text = "%sRecipient: %s\n".formatted(message, phoneNumber);
        var msg = SendMessage.builder()
                .text(text)
                .chatId(config.getChatId())
                .messageThreadId(config.getChatThreadId())
                .build();

        try {
            sender.execute(msg);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }
}
