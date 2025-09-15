package most.qms.repositories;

import most.qms.models.Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// ToDo: Make indexes for this table
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("""
            SELECT g 
            FROM Group g 
            WHERE g.status = most.qms.models.GroupStatus.WAITING 
            AND SIZE(g.tickets) < 10
            ORDER BY g.createdAt ASC 
            """)
    Optional<Group> findNotFullAvailable();

    @Query("""
            SELECT g 
            FROM Group g 
            WHERE g.status = most.qms.models.GroupStatus.CALLED 
            ORDER BY g.calledAt DESC 
            """)
    Optional<Group> findLastCalled();

    @Query(
            """
            SELECT g 
            FROM Group g 
            WHERE g.status = most.qms.models.GroupStatus.WAITING  
            ORDER BY g.createdAt ASC 
            """
    )
    Optional<Group> findNextForCalling();


    @EntityGraph(attributePaths = {"tickets"})
    Optional<Group> findById(Long id);
}
