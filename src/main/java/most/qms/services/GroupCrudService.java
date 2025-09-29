package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.config.AppConfig;
import most.qms.interfaces.GroupCrud;
import most.qms.models.Group;
import most.qms.repositories.GroupRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional(readOnly = true)
public class GroupCrudService implements GroupCrud {
    private final Logger log = getLogger(GroupCrudService.class);
    private final AppConfig config;
    private final GroupRepository groupRepo;

    @Autowired
    public GroupCrudService(AppConfig config, GroupRepository groupRepo) {
        this.config = config;
        this.groupRepo = groupRepo;
    }

    public Group findById(Long id) {
        return groupRepo
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Group with id=%d not found!".formatted(id)));
    }

    @Transactional
    public Group save(Group group) {
        return groupRepo.save(group);
    }


    @Transactional
    public List<Group> saveAll(Collection<Group> groups) {
        return groupRepo
                .saveAll(groups);
    }

    @Override
    public Group getLastAvailable() {
        return groupRepo
                .findNotFullAvailable(config.getGroupCapacity())
                .orElseGet(() -> groupRepo.save(new Group()));
    }

    @Override
    public Optional<Group> findLastCalled() {
        return groupRepo
                .findLastCalled();
    }

    @Override
    public Optional<Group> findNextForCalling() {
        return groupRepo
                .findNextForCalling();
    }
}
