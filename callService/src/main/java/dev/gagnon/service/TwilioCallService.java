package dev.gagnon.service;

import com.twilio.Twilio;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;
import com.twilio.rest.video.v1.Room;
import dev.gagnon.config.WebSocketEventListener;
import dev.gagnon.dto.CallRequest;
import dev.gagnon.dto.CallResponse;
import dev.gagnon.model.Call;
import dev.gagnon.repository.CallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioCallService {

    private final CallRepository callRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.api-key}")
    private String apiKey;

    @Value("${twilio.api-secret}")
    private String apiSecret;

    @Value("${call.timeout-seconds:45}")
    private int callTimeoutSeconds;

    private final WebSocketEventListener webSocketEventListener;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(apiKey, apiSecret, accountSid);
    }

    @Transactional
    public CallResponse initiateCall(CallRequest request) {
        try {
            // Create unique room name
            String roomName = "call-" + UUID.randomUUID().toString();
            
            // Create Twilio room
            Room room = Room.creator()
                    .setUniqueName(roomName)
                    .setType(Room.RoomType.GROUP)
                    .create();
            
            String roomSid = room.getSid();
            // Save call to database
            Call call = new Call();
            call.setCallerEmail(request.getCallerEmail());
            call.setCallerName(request.getCallerName());
            call.setVendorEmail(request.getVendorEmail());
            call.setVendorName(request.getVendorName());
            call.setCallType(Call.CallType.valueOf(request.getCallType()));
            call.setStatus(Call.CallStatus.RINGING);
            call.setTwilioRoomSid(roomSid);
            call.setTwilioRoomName(roomName);

            call = callRepository.save(call);

            // Generate access token for caller
            String callerToken = generateAccessToken(request.getCallerEmail(), roomName);

            // Send notification to vendor via WebSocket
            sendCallNotification(call);

            // Convert to response
            CallResponse response = convertToResponse(call);
            response.setAccessToken(callerToken);

            log.info("Call initiated: {} -> {}", request.getCallerEmail(), request.getVendorEmail());
            return response;

        } catch (Exception e) {
            log.error("Failed to initiate call: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate call", e);
        }
    }

    @Transactional
    public CallResponse answerCall(Long callId, String vendorEmail) {
        try {
            Call call = callRepository.findById(callId)
                    .orElseThrow(() -> new RuntimeException("Call not found"));

            if (!call.getVendorEmail().equals(vendorEmail)) {
                throw new RuntimeException("Unauthorized to answer this call");
            }

            if (call.getStatus() != Call.CallStatus.RINGING) {
                throw new RuntimeException("Call is not in ringing state");
            }

            // Update call status
            call.setStatus(Call.CallStatus.ANSWERED);
            call.setAnsweredAt(LocalDateTime.now());
            call = callRepository.save(call);

            // Generate access token for vendor
            String vendorToken = generateAccessToken(vendorEmail, call.getTwilioRoomName());

            // Notify caller that call was answered
            try {
                log.info("Sending call answered notification to caller: {}", call.getCallerEmail());
                messagingTemplate.convertAndSendToUser(
                        call.getCallerEmail(),
                        "/queue/call-answered",
                        convertToResponse(call)
                );
                log.info("Call answered notification sent successfully to: {}", call.getCallerEmail());
            } catch (Exception e) {
                log.error("Failed to send call answered notification: {}", e.getMessage(), e);
            }

            CallResponse response = convertToResponse(call);
            response.setAccessToken(vendorToken);

            log.info("Call answered: {} by {}", callId, vendorEmail);
            return response;

        } catch (Exception e) {
            log.error("Failed to answer call: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to answer call", e);
        }
    }

    public List<CallResponse> getPendingCalls(String vendorEmail) {
        List<Call> calls = callRepository.findByVendorEmailAndStatusOrderByCreatedAtDesc(
                vendorEmail, Call.CallStatus.RINGING);
        return calls.stream().map(this::convertToResponse).toList();
    }

    @Transactional
    public void rejectCall(Long callId, String vendorEmail) {
        try {
            Call call = callRepository.findById(callId)
                    .orElseThrow(() -> new RuntimeException("Call not found"));

            if (!call.getVendorEmail().equals(vendorEmail)) {
                throw new RuntimeException("Unauthorized to reject this call");
            }

            // Update call status
            call.setStatus(Call.CallStatus.REJECTED);
            call.setEndedAt(LocalDateTime.now());
            callRepository.save(call);

            // End Twilio room
            endTwilioRoom(call.getTwilioRoomSid());

            // Notify caller that call was rejected
            try {
                log.info("Sending call rejected notification to caller: {}", call.getCallerEmail());
                messagingTemplate.convertAndSendToUser(
                        call.getCallerEmail(),
                        "/queue/call-rejected",
                        convertToResponse(call)
                );
                log.info("Call rejected notification sent successfully to: {}", call.getCallerEmail());
            } catch (Exception e) {
                log.error("Failed to send call rejected notification: {}", e.getMessage(), e);
            }

            log.info("Call rejected: {} by {}", callId, vendorEmail);

        } catch (Exception e) {
            log.error("Failed to reject call: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reject call", e);
        }
    }

    @Transactional
    public void endCall(Long callId, String userEmail) {
        try {
            Call call = callRepository.findById(callId)
                    .orElseThrow(() -> new RuntimeException("Call not found"));

            if (!call.getCallerEmail().equals(userEmail) && !call.getVendorEmail().equals(userEmail)) {
                throw new RuntimeException("Unauthorized to end this call");
            }

            // Update call status
            call.setStatus(Call.CallStatus.ENDED);
            call.setEndedAt(LocalDateTime.now());
            callRepository.save(call);

            // End Twilio room
            endTwilioRoom(call.getTwilioRoomSid());

            // Notify other participant
            String otherParticipant = call.getCallerEmail().equals(userEmail) 
                    ? call.getVendorEmail() 
                    : call.getCallerEmail();

            messagingTemplate.convertAndSendToUser(
                    otherParticipant,
                    "/queue/call-ended",
                    convertToResponse(call)
            );

            log.info("Call ended: {} by {}", callId, userEmail);

        } catch (Exception e) {
            log.error("Failed to end call: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to end call", e);
        }
    }

    public List<CallResponse> getCallHistory(String userEmail) {
        List<Call> calls = callRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        return calls.stream().map(this::convertToResponse).toList();
    }

    public List<CallResponse> getVendorCalls(String vendorEmail, Call.CallStatus status) {
        List<Call> calls;
        if (status != null) {
            calls = callRepository.findByVendorEmailAndStatusOrderByCreatedAtDesc(vendorEmail, status);
        } else {
            calls = callRepository.findByVendorEmailOrderByCreatedAtDesc(vendorEmail);
        }
        return calls.stream().map(this::convertToResponse).toList();
    }

    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    @Transactional
    public void handleExpiredCalls() {
        LocalDateTime timeout = LocalDateTime.now().minusSeconds(callTimeoutSeconds);
        List<Call> expiredCalls = callRepository.findExpiredRingingCalls(timeout);

        for (Call call : expiredCalls) {
            try {
                log.info("Handling expired call: {} - Created at: {}", call.getId(), call.getCreatedAt());

                call.setStatus(Call.CallStatus.MISSED);
                call.setEndedAt(LocalDateTime.now());
                call = callRepository.save(call);

                // End Twilio room
                endTwilioRoom(call.getTwilioRoomSid());

                // Notify caller that call was missed
                try {
                    messagingTemplate.convertAndSendToUser(
                            call.getCallerEmail(),
                            "/queue/call-missed",
                            convertToResponse(call)
                    );
                    log.info("Call missed notification sent to caller: {}", call.getCallerEmail());
                } catch (Exception e) {
                    log.warn("Failed to send missed call notification to {}: {}",
                            call.getCallerEmail(), e.getMessage());
                }

                log.info("Call expired and marked as missed: {}", call.getId());

            } catch (Exception e) {
                log.error("Error handling expired call {}: {}", call.getId(), e.getMessage(), e);
            }
        }

        if (!expiredCalls.isEmpty()) {
            log.info("Processed {} expired calls", expiredCalls.size());
        }
    }

    private String generateAccessToken(String identity, String roomName) {
        VideoGrant videoGrant = new VideoGrant();
        videoGrant.setRoom(roomName);

        AccessToken token = new AccessToken.Builder(accountSid, apiKey, apiSecret)
                .identity(identity)
                .grant(videoGrant)
                .build();

        return token.toJwt();
    }

    private void sendCallNotification(Call call) {
        try {
            CallResponse response = convertToResponse(call);

            // Check if vendor is connected via WebSocket
                messagingTemplate.convertAndSendToUser(
                        call.getVendorEmail(),
                        "/queue/incoming-call",
                        response
                );
                log.info("Call notification sent via WebSocket to: {}", call.getVendorEmail());
        } catch (Exception e) {
            log.error("Failed to send call notification to {}: {}", call.getVendorEmail(), e.getMessage(), e);
        }
    }

    private void endTwilioRoom(String roomSid) {
        try {
            if (roomSid != null) {
                Room.updater(roomSid, Room.RoomStatus.COMPLETED).update();
                log.info("Twilio room {} ended successfully", roomSid);
            }
        } catch (Exception e) {
            log.warn("Failed to end Twilio room {}: {}", roomSid, e.getMessage());
        }
    }

    private CallResponse convertToResponse(Call call) {
        CallResponse response = new CallResponse();
        response.setId(call.getId());
        response.setCallerEmail(call.getCallerEmail());
        response.setCallerName(call.getCallerName());
        response.setVendorEmail(call.getVendorEmail());
        response.setVendorName(call.getVendorName());
        response.setCallType(call.getCallType());
        response.setStatus(String.valueOf(call.getStatus()));
        response.setTwilioRoomSid(call.getTwilioRoomSid());
        response.setTwilioRoomName(call.getTwilioRoomName());
        response.setCreatedAt(call.getCreatedAt());
        response.setAnsweredAt(call.getAnsweredAt());
        response.setEndedAt(call.getEndedAt());
        return response;
    }
}