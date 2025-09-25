package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.models.Group;
import most.qms.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static most.qms.models.TicketStatus.WAITING;

@Service
@Transactional(readOnly = true)
public class GroupService {
    @Value("${group.capacity}")
    private long groupCapacity;
    private final GroupRepository groupRepo;

    @Autowired
    public GroupService(GroupRepository groupRepo) {
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
    public Group saveAll(Collection<Group> groups) {
        return groupRepo
                .saveAll(groups)
                .getLast();
    }

    public void createNewGroupIfPreviousFull(Group group) {
        long currentCapacity = group
                .getTickets()
                .stream()
                .filter(t -> t.getStatus() == WAITING)
                .count();
        if (currentCapacity == groupCapacity){
            this.getLastAvailable();
        }
    }


    public Group getLastAvailable() {
        return groupRepo
                .findNotFullAvailable(groupCapacity)
                .orElseGet(() -> groupRepo.save(new Group()));
    }

    public Group findLastCalled() {
        return groupRepo
                .findLastCalled()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Last called group not found!"));
    }

    public Group findNextForCalling() {
        return groupRepo
                .findNextForCalling()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Next calling group not found!"
                ));
    }
}
