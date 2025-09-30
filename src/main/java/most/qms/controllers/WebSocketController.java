package most.qms.controllers;

import most.qms.dtos.responses.UpdatedTicketDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendTicketToUser(String phoneNumber, UpdatedTicketDto message) {
        log.error("sendTicketToUser" + message);
        template.convertAndSendToUser(
                phoneNumber,
                "/queue/updates",
                message);
    }
}
