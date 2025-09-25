package most.qms.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.DAYS;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expired.days}")
    private int expiredDays;

    @Getter
    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = hmacShaKeyFor(BASE64.decode(secret));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

        return builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(new Date(currentTimeMillis() + DAYS.toMillis(expiredDays)))
                .signWith(signingKey)
                .compact();
    }


}
