package most.qms.controllers;

import jakarta.transaction.Transactional;
import most.qms.dtos.responses.UpdatedTicketDto;
import most.qms.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;

import static most.qms.dtos.responses.UpdatedTicketDto.from;

@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
    private final SimpMessagingTemplate template;
    private final TicketRepository ticketRepository;

    @Autowired
    public WebSocketController(SimpMessagingTemplate template, TicketRepository ticketRepository) {
        this.template = template;
        this.ticketRepository = ticketRepository;
    }

    public void sendTicket(String username, UpdatedTicketDto ticket) {
        log.error("sendTicket {} ToUser {}", ticket, username);
        template.convertAndSendToUser(
                username, // username из SecurityContext
                "/queue/tickets",
                ticket
        );
    }

    @GetMapping("/api/test_message")
    @Transactional
    public ResponseEntity<String> sendMessage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var ticket = ticketRepository.findById(1L);
            if (ticket.isPresent()) {

                var updatedTicketDto =
                        from(ticket.get(), 0L, Duration.ZERO);

                this.sendTicket(username, updatedTicketDto);

                return ResponseEntity.ok("Sended");
            }
            else {
                return ResponseEntity.notFound().build();
            }
    }
}
