package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.requests.LoginRequest;
import most.qms.exceptions.AuthException;
import most.qms.models.User;
import most.qms.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static most.qms.models.UserStatus.PENDING;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.security.oauth2.jwt.JwtClaimsSet.builder;
import static org.springframework.security.oauth2.jwt.JwtEncoderParameters.from;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder, JwtEncoder jwtEncoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<String> login(LoginRequest login) {
        String phoneNumber = login.getPhoneNumber();
        User user = userRepo
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User with phone number '%s' not found!"
                                        .formatted(phoneNumber)));

        if (user.getStatus().equals(PENDING)) {
            throw new AuthException("User with phone number '%s' is not verified!");
        }
        if (!login.getPassword().equals(user.getPassword())) {
            return status(FORBIDDEN).body("Invalid password!");
        }

        JwtClaimsSet claims = builder()
                .subject(user.getId().toString())
                .claim("role", "ROLE_USER")
                .issuedAt(now())
                .expiresAt(now().plus(1, HOURS))
                .build();

        return ok(jwtEncoder.encode(from(claims)).getTokenValue());
    }
}
