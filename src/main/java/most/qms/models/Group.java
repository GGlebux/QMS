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
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static most.qms.models.Status.WAITING;

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
    private Status status = WAITING;

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
                        .format(ISO_DATE_TIME));
    }
}
