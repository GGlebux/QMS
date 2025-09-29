package most.qms.repositories;

import most.qms.models.Ticket;
import most.qms.models.TicketStatus;
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
    @EntityGraph(attributePaths = {"group"})
    @Query(value = """
            SELECT count(t) + 1 
            FROM Ticket t 
            WHERE t.status = most.qms.models.TicketStatus.WAITING 
            AND t.id < (
            SELECT ot.id 
            FROM Ticket ot 
            WHERE ot.user.id = :user_id 
            AND ot.group.id = :group_id 
            AND ot.status = most.qms.models.TicketStatus.WAITING
            )
            """)
    Long findTicketsAhead(@Param("user_id") Long userId,
                          @Param("group_id") Long groupId);


    @Query(
            "SELECT t " +
            "FROM Ticket t " +
            "WHERE t.user.id = :userId " +
            "AND (t.status = most.qms.models.TicketStatus.WAITING " +
            "OR t.status = most.qms.models.TicketStatus.CALLED)"
    )
    Optional<Ticket> findActiveByUserId(@Param("userId") Long userId);

    Boolean existsByUserAndStatusIn(User user, EnumSet<TicketStatus> ticketStatuses);
}
