package dev.gagnon.controller;

import dev.gagnon.dto.MarkReadRequest;
import dev.gagnon.dto.SendMessageRequest;
import dev.gagnon.model.ChatConversation;
import dev.gagnon.model.ChatMessage;
import dev.gagnon.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody SendMessageRequest request) {
        ChatMessage message = chatService.sendMessage(request);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(
        @RequestParam String user1, 
        @RequestParam String user2
    ) {
        List<ChatMessage> messages = chatService.getMessages(user1, user2);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/conversations/{userEmail}")
    public ResponseEntity<List<ChatConversation>> getConversations(@PathVariable String userEmail) {
        List<ChatConversation> conversations = chatService.getConversations(userEmail);
        return ResponseEntity.ok(conversations);
    }
    
    @PutMapping("/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(@RequestBody MarkReadRequest request) {
        chatService.markMessagesAsRead(request.getFromEmail(), request.getToEmail());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/user/{userEmail}/online")
    public ResponseEntity<Void> setUserOnline(@PathVariable String userEmail) {
        chatService.setUserOnline(userEmail);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/user/{userEmail}/offline")
    public ResponseEntity<Void> setUserOffline(@PathVariable String userEmail) {
        chatService.setUserOffline(userEmail);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/user/{userEmail}/online-status")
    public ResponseEntity<Boolean> getUserOnlineStatus(@PathVariable String userEmail) {
        boolean isOnline = chatService.isUserOnline(userEmail);
        return ResponseEntity.ok(isOnline);
    }

    @GetMapping("/debug/all-messages")
    public ResponseEntity<List<ChatMessage>> getAllMessagesForDebug() {
        List<ChatMessage> allMessages = chatService.getAllMessagesForDebug();
        return ResponseEntity.ok(allMessages);
    }

    @GetMapping("/debug/messages-by-conversation/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByConversationId(@PathVariable String conversationId) {
        List<ChatMessage> messages = chatService.getMessagesByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiateChat(@RequestBody Map<String, Object> request) {
        try {
            String buyerEmail = (String) request.get("buyerEmail");
            String vendorEmail = (String) request.get("vendorEmail");
            Long productId = request.get("productId") != null ? Long.valueOf(request.get("productId").toString()) : null;
            Long shopId = request.get("shopId") != null ? Long.valueOf(request.get("shopId").toString()) : null;
            String productName = (String) request.get("productName");
            String shopName = (String) request.get("shopName");

            String conversationId = generateConversationId(buyerEmail, vendorEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("conversationId", conversationId);
            response.put("buyerEmail", buyerEmail);
            response.put("vendorEmail", vendorEmail);
            response.put("productId", productId);
            response.put("shopId", shopId);
            response.put("productName", productName);
            response.put("shopName", shopName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/unread-count/{userEmail}")
    public ResponseEntity<Map<String, Integer>> getUnreadMessagesCount(@PathVariable String userEmail) {
        int unreadCount = chatService.getTotalUnreadMessagesCount(userEmail);
        Map<String, Integer> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/mark-all-read/{userEmail}")
    public ResponseEntity<Void> markAllMessagesAsRead(@PathVariable String userEmail) {
        chatService.markAllMessagesAsReadForUser(userEmail);
        return ResponseEntity.ok().build();
    }

    private String generateConversationId(String email1, String email2) {
        String safeEmail1 = email1.replace("@", "-at-").replace(".", "-dot-");
        String safeEmail2 = email2.replace("@", "-at-").replace(".", "-dot-");
        
        List<String> emails = Arrays.asList(safeEmail1, safeEmail2);
        Collections.sort(emails);
        return "chat-" + String.join("_", emails);
    }
}