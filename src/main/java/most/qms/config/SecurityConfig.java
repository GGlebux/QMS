package most.qms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import most.qms.exceptions.EntityNotFoundException;
import most.qms.repositories.UserRepository;
import most.qms.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withSecretKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public SecurityConfig(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(this.authenticationEntryPoint())
                        .accessDeniedHandler(this.accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**",
                                "/api/verification/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws-client.html",
                                "/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                        )
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return withSecretKey(jwtService.getSigningKey()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByPhoneNumber(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Username not found: %s".formatted(username)));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService usd, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usd);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return (req, response, authException) -> {
            response.setStatus(SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, Object> body = Map.of(
                    "error", authException.getClass().getSimpleName(),
                    "message", authException.getMessage()
            );
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (req, response, accessDeniedException) -> {
            response.setStatus(SC_FORBIDDEN);
            response.setContentType("application/json");
            Map<String, Object> body = Map.of(
                    "error", accessDeniedException.getClass().getSimpleName(),
                    "message", accessDeniedException.getMessage()
            );
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        };
    }

}
