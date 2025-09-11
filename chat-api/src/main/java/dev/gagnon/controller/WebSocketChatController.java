package dev.gagnon.controller;

import dev.gagnon.dto.MarkReadRequest;
import dev.gagnon.dto.SendMessageRequest;
import dev.gagnon.model.ChatMessage;
import dev.gagnon.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Send message through the chat service
            ChatMessage message = chatService.sendMessage(request);
            System.out.println("Message sent via WebSocket: " + message.getId());
        } catch (Exception e) {
            System.err.println("Error sending message via WebSocket: " + e.getMessage());
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String userEmail = payload.get("userEmail");
        if (userEmail != null) {
            // Set user as online
            chatService.setUserOnline(userEmail);
            
            // Store username in WebSocket session
            headerAccessor.getSessionAttributes().put("userEmail", userEmail);
            
            System.out.println("User connected via WebSocket: " + userEmail);
        }
    }

    @MessageMapping("/chat.markRead")
    public void markMessagesAsRead(@Payload MarkReadRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            chatService.markMessagesAsRead(request.getFromEmail(), request.getToEmail());
            System.out.println("Messages marked as read via WebSocket");
        } catch (Exception e) {
            System.err.println("Error marking messages as read via WebSocket: " + e.getMessage());
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String conversationId = payload.get("conversationId");
        String userEmail = payload.get("userEmail");
        boolean isTyping = Boolean.parseBoolean(payload.get("isTyping"));
        
        if (conversationId != null && userEmail != null) {
            // Broadcast typing status to conversation
            chatService.broadcastTypingStatus(conversationId, userEmail, isTyping);
        }
    }
}