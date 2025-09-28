package most.qms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;
import static most.qms.models.TicketStatus.WAITING;

@Entity
@Table(name = "ticket",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "number"})
        },
        indexes = {
                @Index(columnList = "status, number", name = "idx_ticket_status_number"),
        })
@Setter
@Getter
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @Column(name = "number", nullable = false, unique = true, updatable = false)
    private Long number;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status = WAITING;

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
