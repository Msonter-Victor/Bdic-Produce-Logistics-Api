package dev.gagnon.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRoomDTO {
    private Long roomId;
    private String buyerUsername;
    private String sellerUsername;
    private Long productId;
    private LocalDateTime createdAt;
    private long unreadCount;
}