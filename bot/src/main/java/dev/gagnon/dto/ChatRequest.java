package dev.gagnon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;
    
    @NotBlank(message = "Session ID cannot be blank")
    private String sessionId;
    
    @NotNull(message = "Conversation state cannot be null")
    private String conversationState; // INITIAL, PRODUCT_SEARCH, MARKET_SELECTION, SHOP_RESULTS
}