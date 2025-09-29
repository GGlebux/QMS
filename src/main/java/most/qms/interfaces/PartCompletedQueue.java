package most.qms.interfaces;

import most.qms.models.Group;

public interface PartCompletedQueue {
    void callIfPartOfGroupComplete(Group group);
}
