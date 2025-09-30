package most.qms.services;

import most.qms.interfaces.GroupCrud;
import most.qms.interfaces.GroupOperation;
import most.qms.interfaces.QueueOperation;
import most.qms.interfaces.TicketUpdater;
import most.qms.models.Group;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional(readOnly = true)
public class QueueService implements QueueOperation {
    private static final Logger log = getLogger(QueueService.class);
    private final GroupCrud groupCrud;
    private final GroupOperation groupOperations;
    private final TicketUpdater updater;

    @Autowired
    public QueueService(GroupCrud groupCrud,
                        GroupOperation groupOperations, TicketUpdater updater) {
        this.groupCrud = groupCrud;
        this.groupOperations = groupOperations;
        this.updater = updater;
    }

    @Override
    @Transactional
    public String callNextGroup(Optional<Group> source) {
        // Обрабатываем последнюю как выполненную
        String previousOutput = this.processPrevious(source);

        // Обрабатываем следующую для вызова
        String nextOutput = this.processNext();

        updater.updateAllTickets();

        return "%s\n%s".formatted(previousOutput, nextOutput);
    }

    @Override
    public String processPrevious(Optional<Group> source) {
        String output;
        // Если передали пустой source - пробуем найти
        var lastCalled = source.or(groupCrud::findLastCalled);
        if (lastCalled.isPresent()) {
            Group group = lastCalled.get();
            groupOperations.completeGroup(group);
            output = "Group '%s' has been successfully completed!"
                    .formatted(group.getName());
            log.info(output);
            return output;
        }
        output = "Last called group not found!";
        log.info(output);
        return output;
    }

    @Override
    public String processNext() {
        String output;
        Optional<Group> nextGroup = groupCrud.findNextForCalling();
        if (nextGroup.isEmpty()) {
            output = "No group found for calling!";
            log.info(output);
            return output;
        }
        if (nextGroup.get().getTickets().isEmpty()) {
            output = "Group '%s' is empty!"
                    .formatted(nextGroup.get().getName());
            log.info(output);
            return output;
        }
        output = "Called next group '%s'!"
                .formatted(nextGroup.get().getName());
        groupOperations.callGroup(nextGroup.get());
        updater.callTickets(new ArrayList<>(nextGroup.get().getTickets()));
        log.info(output);
        return output;
    }
}
