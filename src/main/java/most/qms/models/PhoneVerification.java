package most.qms.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;

@Entity
@Table(name = "phone_verification", indexes = @Index(columnList = "phone_number"))
@Data
@NoArgsConstructor
public class PhoneVerification {
    @Id
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt = now().plusMinutes(5);

    public Boolean isExpired() {
        return expiresAt.isBefore(now());
    }

    public PhoneVerification(String phoneNumber, String code) {
        this.phoneNumber = phoneNumber;
        this.code = code;
    }
}
