package most.qms.repositories;

import most.qms.models.Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

// ToDo: Make indexes for this table
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("""
            SELECT g
            FROM Group g
            WHERE g.status = most.qms.models.GroupStatus.WAITING
            AND SIZE(g.tickets) < :capacity
            ORDER BY g.createdAt ASC
            LIMIT 1
            """)
    Optional<Group> findNotFullAvailable(@Param("capacity") Long capacity);

    @Query("""
            SELECT g
            FROM Group g
            WHERE g.status = most.qms.models.GroupStatus.CALLED
            ORDER BY g.calledAt DESC
            LIMIT 1
            """)
    Optional<Group> findLastCalled();

    @Query(
            """
            SELECT g
            FROM Group g
            WHERE g.status = most.qms.models.GroupStatus.WAITING
            ORDER BY g.createdAt ASC
            LIMIT 1
            """
    )
    Optional<Group> findNextForCalling();

    @Query("""
            SELECT COUNT(g)
            FROM Group g
            WHERE g.status = most.qms.models.GroupStatus.WAITING
            AND g.createdAt < :current
    """)
    Long countWaitingAhead(@Param("current") LocalDateTime current);



    @EntityGraph(attributePaths = {"tickets"})
    Optional<Group> findById(Long id);

    Group findByNameContainsIgnoreCase(String filterValue);
}
