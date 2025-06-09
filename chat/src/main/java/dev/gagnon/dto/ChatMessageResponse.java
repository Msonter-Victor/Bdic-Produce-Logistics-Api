package dev.gagnon.dto;

import dev.gagnon.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String senderUsername;
    private String recipientUsername;
    private String content;
    private LocalDateTime timestamp;
    
    public static ChatMessageResponse fromEntity(ChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getSenderUsername(),
            message.getRecipientUsername(),
            message.getContent(),
            message.getTimestamp()
        );
    }
}