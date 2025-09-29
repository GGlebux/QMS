package most.qms.events;

import most.qms.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class QueueEventListener {
    private final QueueService queueService;

    @Autowired
    public QueueEventListener(QueueService queueService) {
        this.queueService = queueService;
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleNextGroup(NextGroupEvent event) {
        queueService.callNextGroup(event.getGroup());
    }

    @EventListener
    private void updateQueueHandler(UpdateQueueEvent event) {

    }
}
