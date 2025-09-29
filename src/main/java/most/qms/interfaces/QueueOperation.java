package most.qms.interfaces;

import most.qms.models.Group;

import java.util.Optional;

public interface QueueOperation {
    String callNextGroup(Optional<Group> source);

    String processPrevious(Optional<Group> source);

    String processNext();
}
