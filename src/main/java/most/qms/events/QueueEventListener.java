package most.qms.events;

import most.qms.controllers.WebSocketController;
import most.qms.interfaces.QueueOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Component
public class QueueEventListener {
    private final QueueOperation queue;
    private final WebSocketController websocket;

    @Autowired
    public QueueEventListener(QueueOperation queue, WebSocketController websocket) {
        this.queue = queue;
        this.websocket = websocket;
    }

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void handleNextGroup(NextGroupEvent event) {
        queue.callNextGroup(event.getGroup());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    private void updateQueueHandler(UpdateWebSocketEvent event) {
        websocket.sendTicket(
                event.getUsername(),
                event.getTicket()
        );
    }
}
