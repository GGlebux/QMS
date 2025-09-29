package most.qms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static most.qms.models.GroupStatus.*;

@Entity
@Table(name = "\"group\"")
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private GroupStatus status = WAITING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = now();

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;


    @OneToMany(mappedBy = "group", fetch = EAGER)
    private Set<Ticket> tickets = new HashSet<>();


    public Group() {
        this.name = "Group № %s"
                .formatted(createdAt
                        .format(ofPattern("HH:mm d MMM yyyy")));
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setGroup(this);
    }

    public void call() {
        this.status = CALLED;
        this.calledAt = now();
    }

    public void complete() {
        this.status = COMPLETE;
        this.completedAt = now();
    }
}
