package dev.gagnon.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "chats")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String senderId, recipientId, content;
    private LocalDateTime timestamp;

    // getters, setters, no-args and all-args constructor
    public ChatMessage() {}
    public ChatMessage(String senderId, String recipientId, String content, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = timestamp;
    }
}
