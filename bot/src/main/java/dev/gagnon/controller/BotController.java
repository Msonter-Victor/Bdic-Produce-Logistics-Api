package dev.gagnon.controller;

import dev.gagnon.dto.ChatRequest;
import dev.gagnon.dto.ChatResponse;
import dev.gagnon.service.EnhancedVirtualMarketAssistantService;
import dev.gagnon.service.DataSynchronizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
@Slf4j
public class BotController {
    
    private final EnhancedVirtualMarketAssistantService assistantService;
    private final DataSynchronizationService dataSynchronizationService;
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        try {
            log.info("Received chat request: sessionId={}, message={}, state={}", 
                    request.getSessionId(), request.getMessage(), request.getConversationState());
            
            ChatResponse response = assistantService.processMessage(request);
            
            log.info("Sending chat response: state={}, hasShops={}", 
                    response.getConversationState(), 
                    response.getShopResults() != null && !response.getShopResults().isEmpty());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ResponseEntity.ok(new ChatResponse(
                "I'm sorry, I encountered an error. Please try again.",
                "INITIAL",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                true,
                null
            ));
        }
    }
    
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        try {
            assistantService.clearSession(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing session: " + sessionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Bot service is running");
    }
    
    @PostMapping("/sync")
    public ResponseEntity<String> syncData() {
        try {
            log.info("Manual data synchronization requested");
            dataSynchronizationService.performFullSync();
            return ResponseEntity.ok("Data synchronization completed successfully");
        } catch (Exception e) {
            log.error("Error during manual sync", e);
            return ResponseEntity.internalServerError().body("Sync failed: " + e.getMessage());
        }
    }
}