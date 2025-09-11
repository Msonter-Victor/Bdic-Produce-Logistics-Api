package dev.gagnon.model;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_memory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMemory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String sessionId;
    
    @Column
    private String userEmail;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private String messageType; // USER, ASSISTANT, SYSTEM
    
    @Column(columnDefinition = "vector(1536)") // OpenAI embedding dimension
    private PGvector embedding;
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON string for additional context
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}