package dev.gagnon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String roomId;
    private String senderUsername;
    private String senderName;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
    private Long productId;
}