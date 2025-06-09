package dev.gagnon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    private Long roomId;
    
    private String buyerUsername;
    private String sellerUsername;
    private Long productId;
    private LocalDateTime createdAt = LocalDateTime.now();
}