package most.qms.config;

import most.qms.exceptions.EntityNotFoundException;
import most.qms.models.User;
import most.qms.repositories.UserRepository;
import most.qms.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AppConfig appConfig;


    @Autowired
    public WebSocketConfig(UserRepository userRepository, JwtService jwtService, AppConfig config) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.appConfig = config;
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins(appConfig.getFrontUrl())
                .addInterceptors(this.handshakeInterceptor())
                .setHandshakeHandler(this.customHandshakeHandler())
                .withSockJS();
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor() {
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
        };
    }

    @Bean
    public AbstractHandshakeHandler customHandshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request,
                                              WebSocketHandler wsHandler,
                                              Map<String, Object> attributes) {
                return (Principal) attributes.get("principal");
            }
        };
    }
}
