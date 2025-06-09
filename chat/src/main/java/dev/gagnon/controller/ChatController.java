package dev.gagnon.controller;

import dev.gagnon.dto.ChatMessageRequest;
import dev.gagnon.dto.ChatMessageResponse;
import dev.gagnon.model.ChatMessage;
import dev.gagnon.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request) {
        ChatMessage savedMessage = chatService.saveMessage(request);
        ChatMessageResponse response = ChatMessageResponse.fromEntity(savedMessage);
        
        // Send to both participants
        messagingTemplate.convertAndSendToUser(
            request.getRecipientUsername(), 
            "/queue/messages", 
            response
        );
        messagingTemplate.convertAndSendToUser(
            request.getSenderUsername(), 
            "/queue/messages", 
            response
        );
        
        // Also send to the specific chat room
        messagingTemplate.convertAndSend(
            "/topic/chat." + generateChatId(request.getSenderUsername(), request.getRecipientUsername()),
            response
        );
    }

    private String generateChatId(String user1, String user2) {
        return Stream.of(user1, user2)
                   .sorted()
                   .collect(Collectors.joining("_"));
    }
}