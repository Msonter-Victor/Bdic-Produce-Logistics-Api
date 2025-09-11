package dev.gagnon.dto;

import dev.gagnon.model.Call;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CallResponse {
    private Long id;
    private String callerEmail;
    private String callerName;
    private String vendorEmail;
    private String vendorName;
    private Long productId;
    private String productName;
    private Call.CallType callType;
    private String status;
    private String twilioRoomSid;
    private String twilioRoomName;
    private String accessToken;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
    private LocalDateTime endedAt;
}