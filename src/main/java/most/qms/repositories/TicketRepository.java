package most.qms.repositories;

import most.qms.models.Status;
import most.qms.models.Ticket;
import most.qms.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query(value = """
            SELECT count(t)
            FROM Ticket t 
            WHERE t.status = most.qms.models.Status.WAITING 
            AND t.number < (
            SELECT ot.number 
            FROM Ticket ot 
            WHERE ot.user.id = :user_id 
            AND ot.group.id = :group_id 
            AND ot.status = most.qms.models.Status.WAITING
            )
            """)
    Long findTicketsAhead(@Param("user_id") Long userId,
                                     @Param("group_id") Long groupId);



    Boolean existsByUserAndStatusIn(User user, EnumSet<Status> statuses);
}
