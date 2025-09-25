package most.qms.notification;

import most.qms.services.SmsSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MyTelegramBot extends TelegramLongPollingBot implements SmsSender {
    @Value("${telegram.bot.token}")
    private String botToken;
    private final String botUsername = "@most_verify_codes_bot";
    private final String myChatId = "-1002911796169";
    private final int messageThreadId = 303;

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            String text = "%sRecipient: %s\n".formatted(message, phoneNumber);
            var msg = new SendMessage();
            msg.setChatId(myChatId);
            msg.setMessageThreadId(messageThreadId);
            msg.setText(text);
            execute(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
