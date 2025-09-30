package most.qms.interfaces;

import most.qms.models.Group;

import java.util.Collection;
import java.util.Optional;

public interface GroupCrud {
    Group getLastAvailable();

    Optional<Group> findLastCalled();

    Optional<Group> findNextForCalling();

    Long countWaitingAhead(Group group);

    Group save(Group entity);

    Collection<Group> saveAll(Collection<Group> entities);

}
