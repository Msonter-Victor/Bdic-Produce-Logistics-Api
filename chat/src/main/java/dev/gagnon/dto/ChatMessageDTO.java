
package dev.gagnon.dto;

import dev.gagnon.model.MessageType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private String roomId;
    private String senderUsername;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
}