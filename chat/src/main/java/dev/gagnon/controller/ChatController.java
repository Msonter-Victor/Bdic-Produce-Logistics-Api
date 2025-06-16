package dev.gagnon.controller;

import dev.gagnon.model.ChatMessage;
import dev.gagnon.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatRepo;

    // Handle incoming message (STOMP)
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // Set timestamp and persist
        chatMessage.setTimestamp(LocalDateTime.now());
        chatRepo.save(chatMessage);
        // Send to recipient topic
        messagingTemplate.convertAndSend(
            "/topic/messages." + chatMessage.getRecipientId(),
            chatMessage
        );
    }

    // REST: fetch previous messages between currentUser and selectedUser
    @GetMapping("/api/messages/history")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam String user1,
            @RequestParam String user2)
    {
        List<ChatMessage> history = chatRepo
            .findBySenderIdAndRecipientIdOrSenderIdAndRecipientIdOrderByTimestamp(
                user1, user2, user2, user1
            );
        // Optionally sort ascending by timestamp (or reverse here)
        return ResponseEntity.ok(history);
    }

    @GetMapping("/allchats")
    public ResponseEntity<?>getAllChats() {
        List<ChatMessage> chats = chatRepo.findAll();
        return ResponseEntity.ok(chats);
    }
}
