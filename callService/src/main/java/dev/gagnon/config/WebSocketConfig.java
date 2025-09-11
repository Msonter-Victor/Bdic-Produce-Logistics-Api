package dev.gagnon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register /ws endpoint (for direct service access)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // Register /api/ws endpoint (for API Gateway routing)
        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                        message, StompHeaderAccessor.class);

                if (accessor != null) {
                    switch (accessor.getCommand()) {
                        case CONNECT:
                            WebSocketConfig.this.handleConnect(accessor);
                            break;
                        case SUBSCRIBE:
                            WebSocketConfig.this.handleSubscribe(accessor);
                            break;
                        case DISCONNECT:
                            WebSocketConfig.this.handleDisconnect(accessor);
                            break;
                        default:
                            // Handle other commands if needed
                            break;
                    }
                }
                return message;
            }
        });
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String userEmail = accessor.getFirstNativeHeader("userEmail");
        if (userEmail == null) {
            userEmail = accessor.getFirstNativeHeader("X-User-Email");
        }
        if (userEmail == null) {
            userEmail = accessor.getFirstNativeHeader("Authorization");
            if (userEmail != null && userEmail.startsWith("Bearer ")) {
                userEmail = userEmail.substring(7);
            }
        }

        if (userEmail != null && !userEmail.trim().isEmpty()) {
            accessor.setUser(new UsernamePasswordAuthenticationToken(userEmail.trim(), null));
            System.out.println("WebSocket connected successfully for user: " + userEmail);
        } else {
            System.out.println("WebSocket connection rejected - no valid userEmail found");
            throw new SecurityException("Authentication required for WebSocket connection");
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String userEmail = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
        System.out.println("User " + userEmail + " subscribed to: " + destination);

        // Validate subscription permissions
        if (destination != null && destination.startsWith("/user/")) {
            String targetUser = destination.replace("/user/", "").split("/")[0];
            if (!targetUser.equals(userEmail)) {
                throw new SecurityException("Cannot subscribe to other user's queue");
            }
        }
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        String userEmail = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
        System.out.println("User " + userEmail + " disconnected from WebSocket");
    }
}