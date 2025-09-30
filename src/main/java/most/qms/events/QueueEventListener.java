package most.qms.events;

import most.qms.controllers.WebSocketController;
import most.qms.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class QueueEventListener {
    private final QueueService queueService;
    private final WebSocketController websocket;

    @Autowired
    public QueueEventListener(QueueService queueService, WebSocketController websocket) {
        this.queueService = queueService;
        this.websocket = websocket;
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleNextGroup(NextGroupEvent event) {
        queueService.callNextGroup(event.getGroup());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    private void updateQueueHandler(UpdateWebSocketEvent event) {
        websocket.sendTicketToUser(
                event.getUsername(),
                event.getTicket()
        );
    }
}
