package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_online_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOnlineStatus {
    @Id
    private String email;
    
    @Column(name = "is_online", nullable = false)
    private boolean isOnline;
    
    @Column(name = "last_seen", nullable = false)
    private LocalDateTime lastSeen;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastSeen = LocalDateTime.now();
    }
}