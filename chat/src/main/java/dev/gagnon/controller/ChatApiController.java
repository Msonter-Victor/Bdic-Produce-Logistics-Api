package dev.gagnon.controller;

import dev.gagnon.dto.ChatConversationResponse;
import dev.gagnon.dto.ChatMessageResponse;
import dev.gagnon.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://marketplace.bdic.ng"})
public class ChatApiController {

    private final ChatService chatService;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(
            @RequestParam String user1,
            @RequestParam String user2) {
        return ResponseEntity.ok(chatService.getChatHistory(user1, user2));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversationResponse>> getUserConversations(
            @RequestParam String username) {
        return ResponseEntity.ok(chatService.getUserConversations(username));
    }
}