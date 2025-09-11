package dev.gagnon.data.model;

import dev.gagnon.data.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String message;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private boolean read = false;
    @ManyToOne
    private User recipient;
    private LocalDateTime createdAt;

}
