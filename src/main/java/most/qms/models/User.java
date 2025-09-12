package most.qms.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Boolean.FALSE;
import static java.time.LocalDateTime.now;
import static most.qms.models.Role.ROLE_USER;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", length = 20, nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(STRING)
    @Column(name = "role", nullable = false, columnDefinition = "")
    private Role role = ROLE_USER;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = now();

    @Column(name = "is_phone_verified", nullable = false)
    private Boolean isPhoneVerified = FALSE;

    // Now that don t need
    @OneToMany(mappedBy = "user", fetch = EAGER, cascade = ALL, orphanRemoval = true)
    private Set<Ticket> tickets;

    @Override
    public String toString() {
        return "User{" +
                "createdAt=" + createdAt +
                ", isPhoneVerified=" + isPhoneVerified +
                ", role=" + role +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
