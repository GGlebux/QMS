package most.qms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;
import static most.qms.models.TicketStatus.*;

@Entity
@Table(name = "ticket",
        indexes = {
                @Index(columnList = "status", name = "idx_ticket_status"),
        })
@Setter
@Getter
@NoArgsConstructor
public class Ticket implements Comparable<Ticket> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status = WAITING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Ticket(User user, Group group) {
        this.user = user;
        this.group = group;
    }

    public void call() {
        this.status = CALLED;
    }

    public void complete() {
        this.status = COMPLETE;
        this.completedAt = now();
    }

    public void cancel() {
        this.status = CANCELED;
        this.group = null;
    }

    public boolean isWaiting() {
        return this.status == WAITING;
    }

    @Override
    public int compareTo(@NotNull Ticket o) {
        return this.createdAt.compareTo(o.createdAt);
    }

}
