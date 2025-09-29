package most.qms.websockets;

import most.qms.dtos.responses.CreatedTicketDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueNotifier {
    private final SimpMessagingTemplate template;

    @Autowired
    public QueueNotifier(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void notify(String username, Long ticketId, CreatedTicketDto dto) {
        template.convertAndSendToUser(
                username,
                "/queue/myTicket",
                dto
        );
    }
}
