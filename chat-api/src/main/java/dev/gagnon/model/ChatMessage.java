package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "from_email", nullable = false)
    private String fromEmail;
    
    @Column(name = "to_email", nullable = false)
    private String toEmail;
    
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "is_read", nullable = false)
    private boolean isRead;
    
    @Column(name = "conversation_id", nullable = false)
    private String conversationId;
    
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "shop_id")
    private Long shopId;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}