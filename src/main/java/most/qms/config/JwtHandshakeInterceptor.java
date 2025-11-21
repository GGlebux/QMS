package most.qms.config;

import most.qms.exceptions.EntityNotFoundException;
import most.qms.models.User;
import most.qms.repositories.UserRepository;
import most.qms.services.JwtService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public JwtHandshakeInterceptor(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = null;

        if (request instanceof ServletServerHttpRequest servletRequest) {
            token = servletRequest.getServletRequest().getParameter("token");
        }

        if (token != null && jwtService.validateToken(token)) {

            String username = jwtService.extractUsername(token);

            User user = userRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            attributes.put("principal", (Principal) () -> username);

            return true;
        }

        return false; // если токен невалидный, handshake запрещён
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
