package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "calls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String callerEmail;
    @Column(nullable = false)
    private String callerName;
    @Column(nullable = false)
    private String vendorEmail;
    @Column(nullable = false)
    private String vendorName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallType callType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus status;

    private String twilioRoomSid;
    private String twilioRoomName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime answeredAt;
    private LocalDateTime endedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum CallType {
        VOICE, VIDEO
    }

    public enum CallStatus {
        INITIATED, RINGING, ANSWERED, REJECTED, MISSED, ENDED
    }
}