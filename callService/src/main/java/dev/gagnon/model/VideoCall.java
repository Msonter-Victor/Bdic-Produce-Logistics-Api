package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_calls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", unique = true, nullable = false)
    private String roomName;

    @Column(name = "buyer_email", nullable = false)
    private String buyerEmail;

    @Column(name = "vendor_email", nullable = false)
    private String vendorEmail;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "shop_id")
    private Long shopId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CallStatus status = CallStatus.INITIATED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "buyer_joined")
    private boolean buyerJoined = false;

    @Column(name = "vendor_joined")
    private boolean vendorJoined = false;

    public enum CallStatus {
        INITIATED,
        RINGING,
        ACTIVE,
        ENDED,
        DECLINED,
        MISSED
    }
}