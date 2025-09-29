package most.qms.interfaces;

import most.qms.models.Group;

public interface GroupOperation {
    void callGroup(Group group);
    void completeGroup(Group group);
}
