package dev.gagnon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationResponse {
    private String otherUser;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    
    public static ChatConversationResponse fromEntity(Object[] tuple) {
        return new ChatConversationResponse(
            (String) tuple[0],
            (String) tuple[1],
            (LocalDateTime) tuple[2]
        );
    }
}