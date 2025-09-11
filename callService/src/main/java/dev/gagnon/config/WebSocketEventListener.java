package dev.gagnon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class WebSocketEventListener {

    // Track active sessions by user email
    private final ConcurrentMap<String, String> activeSessions = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userEmail = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "unknown";
        
        if (userEmail != null && !userEmail.equals("unknown")) {
            activeSessions.put(sessionId, userEmail);
            log.info("🔗 WebSocket CONNECTED - User: {}, Session: {}, Total active: {}", 
                    userEmail, sessionId, activeSessions.size());
        } else {
            log.warn("⚠️ WebSocket connected without user identification - Session: {}", sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userEmail = activeSessions.remove(sessionId);
        
        if (userEmail != null) {
            log.info("🔌 WebSocket DISCONNECTED - User: {}, Session: {}, Total active: {}", 
                    userEmail, sessionId, activeSessions.size());
        } else {
            log.info("🔌 WebSocket DISCONNECTED - Unknown user, Session: {}", sessionId);
        }
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        String userEmail = activeSessions.get(sessionId);
        
        log.info("📡 WebSocket SUBSCRIBE - User: {}, Destination: {}, Session: {}", 
                userEmail != null ? userEmail : "unknown", destination, sessionId);
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userEmail = activeSessions.get(sessionId);
        
        log.info("📡 WebSocket UNSUBSCRIBE - User: {}, Session: {}", 
                userEmail != null ? userEmail : "unknown", sessionId);
    }

    public boolean isUserConnected(String userEmail) {
        return activeSessions.containsValue(userEmail);
    }

    public int getActiveConnectionCount() {
        return activeSessions.size();
    }

    public ConcurrentMap<String, String> getActiveSessions() {
        return new ConcurrentHashMap<>(activeSessions);
    }
}