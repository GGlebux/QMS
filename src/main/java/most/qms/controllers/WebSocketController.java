package most.qms.controllers;

import most.qms.dtos.responses.TicketDto;
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

    public void sendTicket(String username, TicketDto ticket) {
        log.info("sendTicket {} ToUser {}", ticket.getClass().getName(), username);
        template.convertAndSendToUser(
                username,
                "/queue/tickets",
                ticket
        );
    }
}
