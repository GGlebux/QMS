package most.qms.services;

import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.requests.LoginRequest;
import most.qms.dtos.responses.JwtAuthResponse;
import most.qms.exceptions.AuthException;
import most.qms.models.User;
import most.qms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static most.qms.models.UserStatus.PENDING;
import static org.springframework.http.ResponseEntity.ok;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Autowired
    public AuthService(UserRepository userRepo, JwtService jwtService, AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<JwtAuthResponse> login(LoginRequest dto) {
        String phoneNumber = dto.getPhoneNumber();
        User user = userRepo
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User with phone number '%s' not found!"
                                        .formatted(phoneNumber)));

        if (user.getStatus() == PENDING) {
            throw new AuthException("User with phone number '%s' is not verified!"
                    .formatted(phoneNumber));
        }

        UsernamePasswordAuthenticationToken tempAuth = new UsernamePasswordAuthenticationToken(
                dto.getPhoneNumber(),
                dto.getPassword());
        Authentication authentication = authManager
                .authenticate(tempAuth);
        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

        return ok(new JwtAuthResponse(token));
    }
}
