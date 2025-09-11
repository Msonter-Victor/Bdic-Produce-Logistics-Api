package dev.gagnon.dto;

import dev.gagnon.model.ChatMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private String id;
    private String conversationId;
    private String fromEmail;
    private String toEmail;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;
    private Long productId;
    private Long shopId;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.conversationId = chatMessage.getConversationId();
        this.fromEmail = chatMessage.getFromEmail();
        this.toEmail = chatMessage.getToEmail();
        this.message = chatMessage.getMessage();
        this.timestamp = chatMessage.getTimestamp();
        this.isRead = chatMessage.isRead();
        this.productId = chatMessage.getProductId();
        this.shopId = chatMessage.getShopId();
    }
}