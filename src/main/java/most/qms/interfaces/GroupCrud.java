package most.qms.interfaces;

import most.qms.models.Group;

import java.util.Optional;

public interface GroupCrud {
    Group getLastAvailable();
    Optional<Group> findLastCalled();
    Optional<Group> findNextForCalling();
}
