package most.qms.repositories;

import most.qms.models.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @EntityGraph(attributePaths = {"user"})
    @Query("""
            SELECT t
            FROM Ticket t 
            WHERE t.status = most.qms.models.TicketStatus.WAITING
            """)
    List<Ticket> findAllWaiting();


    @Query(
            "SELECT t " +
            "FROM Ticket t " +
            "WHERE t.user.id = :userId " +
            "AND (t.status = most.qms.models.TicketStatus.WAITING " +
            "OR t.status = most.qms.models.TicketStatus.CALLED)"
    )
    Optional<Ticket> findActiveByUserId(@Param("userId") Long userId);

}
