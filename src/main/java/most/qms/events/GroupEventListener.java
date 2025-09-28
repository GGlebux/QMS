package most.qms.events;

import most.qms.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class GroupEventListener {
    private final QueueService queueService;

    @Autowired
    public GroupEventListener(QueueService queueService) {
        this.queueService = queueService;
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleNextGroup(NextGroupEvent nextGroupEvent) {
        queueService.callNextGroup(nextGroupEvent.getGroup());
    }
}
