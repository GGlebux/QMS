package most.qms.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.time.LocalDateTime.now;
import static most.qms.models.Status.WAITING;

@Entity
@Table(name = "ticket",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "number"})
        },
        indexes = {
                @Index(columnList = "status, number", name = "idx_ticket_status_number"),
        })
@Data
@NoArgsConstructor
public class Ticket {
    @EmbeddedId
    private TicketId id;

    @ManyToOne(fetch = LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;



    @Column(name = "number", nullable = false, unique = true, updatable = false)
    private Long number;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private Status status = WAITING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Ticket(User user, Group group, Long number) {
        this.user = user;
        this.group = group;
        // ToDo: counter need reset every day
        this.number = number;
    }
}
