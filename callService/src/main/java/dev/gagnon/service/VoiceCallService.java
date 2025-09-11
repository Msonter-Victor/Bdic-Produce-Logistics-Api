package dev.gagnon.service;

import dev.gagnon.dto.VoiceCallRequest;
import dev.gagnon.dto.VoiceCallResponse;
import dev.gagnon.dto.TwilioTokenResponse;
import dev.gagnon.model.VoiceCall;
import dev.gagnon.repository.VoiceCallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceCallService {

    private final VoiceCallRepository voiceCallRepository;
    private final TwilioService twilioService;
    private final SimpMessagingTemplate messagingTemplate;

    public VoiceCallResponse initiateCall(VoiceCallRequest request) {
        try {
            // Validate that user is not calling themselves
            if (request.getBuyerEmail().equals(request.getVendorEmail())) {
                throw new RuntimeException("Cannot call yourself");
            }

            // Generate unique room name
            String roomName = "voice_room_" + UUID.randomUUID().toString().replace("-", "");

            // Create voice call record
            VoiceCall voiceCall = new VoiceCall();
            voiceCall.setRoomName(roomName);
            voiceCall.setBuyerEmail(request.getBuyerEmail());
            voiceCall.setVendorEmail(request.getVendorEmail());
            voiceCall.setProductId(request.getProductId());
            voiceCall.setShopId(request.getShopId());
            voiceCall.setStatus(VoiceCall.CallStatus.INITIATED);
            voiceCall.setCreatedAt(LocalDateTime.now());

            VoiceCall savedCall = voiceCallRepository.save(voiceCall);
            
            // Notify vendor via WebSocket
            notifyVendorOfIncomingCall(savedCall, request);
            
            // Start 30-second timeout for missed call
            scheduleCallTimeout(savedCall.getId(), roomName);
            
            log.info("Voice call initiated: {} between {} and {}", roomName, request.getBuyerEmail(), request.getVendorEmail());
            
            return new VoiceCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error initiating voice call: {}", e.getMessage());
            throw new RuntimeException("Failed to initiate voice call", e);
        }
    }

    public TwilioTokenResponse joinCall(String roomName, String userEmail) {
        try {
            Optional<VoiceCall> callOpt = voiceCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Voice call not found");
            }

            VoiceCall call = callOpt.get();
            
            // Verify user is authorized to join this call
            if (!call.getBuyerEmail().equals(userEmail) && !call.getVendorEmail().equals(userEmail)) {
                throw new RuntimeException("Unauthorized to join this call");
            }

            // Update join status
            if (call.getBuyerEmail().equals(userEmail)) {
                call.setBuyerJoined(true);
            } else {
                call.setVendorJoined(true);
            }

            // If both users have joined, mark call as active
            if (call.isBuyerJoined() && call.isVendorJoined() && call.getStatus() != VoiceCall.CallStatus.ACTIVE) {
                call.setStatus(VoiceCall.CallStatus.ACTIVE);
                call.setStartedAt(LocalDateTime.now());
            } else if (call.getStatus() == VoiceCall.CallStatus.INITIATED) {
                call.setStatus(VoiceCall.CallStatus.RINGING);
            }

            voiceCallRepository.save(call);

            // Generate Twilio access token with unique identity for voice
            String identity = userEmail.split("@")[0] + "_voice_" + roomName + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
            TwilioTokenResponse tokenResponse = twilioService.generateAccessToken(identity, roomName);

            // Notify other participant about join status
            notifyCallStatusUpdate(call);

            log.info("User {} joined voice call {}", userEmail, roomName);
            
            return tokenResponse;
        } catch (Exception e) {
            log.error("Error joining voice call: {}", e.getMessage());
            throw new RuntimeException("Failed to join voice call", e);
        }
    }

    public VoiceCallResponse endCall(String roomName, String userEmail) {
        try {
            Optional<VoiceCall> callOpt = voiceCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Voice call not found");
            }

            VoiceCall call = callOpt.get();
            
            // Verify user is authorized to end this call
            if (!call.getBuyerEmail().equals(userEmail) && !call.getVendorEmail().equals(userEmail)) {
                throw new RuntimeException("Unauthorized to end this call");
            }

            // Only end if not already ended, declined, or missed
            if (call.getStatus() == VoiceCall.CallStatus.ENDED || 
                call.getStatus() == VoiceCall.CallStatus.DECLINED || 
                call.getStatus() == VoiceCall.CallStatus.MISSED) {
                return new VoiceCallResponse(call);
            }

            // Calculate duration if call was active
            if (call.getStartedAt() != null) {
                long duration = java.time.Duration.between(call.getStartedAt(), LocalDateTime.now()).getSeconds();
                call.setDurationSeconds(duration);
            }

            call.setStatus(VoiceCall.CallStatus.ENDED);
            call.setEndedAt(LocalDateTime.now());

            VoiceCall savedCall = voiceCallRepository.save(call);

            // Notify both participants about call end - this will close modals
            notifyCallEnded(call);

            log.info("Voice call {} ended by {}", roomName, userEmail);
            
            return new VoiceCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error ending voice call: {}", e.getMessage());
            throw new RuntimeException("Failed to end voice call", e);
        }
    }

    public VoiceCallResponse declineCall(String roomName, String userEmail) {
        try {
            Optional<VoiceCall> callOpt = voiceCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Voice call not found");
            }

            VoiceCall call = callOpt.get();
            
            // Only vendor can decline
            if (!call.getVendorEmail().equals(userEmail)) {
                throw new RuntimeException("Only vendor can decline the call");
            }

            // Only decline if not already ended, declined, or missed
            if (call.getStatus() == VoiceCall.CallStatus.ENDED || 
                call.getStatus() == VoiceCall.CallStatus.DECLINED || 
                call.getStatus() == VoiceCall.CallStatus.MISSED) {
                return new VoiceCallResponse(call);
            }

            call.setStatus(VoiceCall.CallStatus.DECLINED);
            call.setEndedAt(LocalDateTime.now());

            VoiceCall savedCall = voiceCallRepository.save(call);

            // Notify both participants about call decline - this will close both modals
            notifyCallDeclined(call);

            log.info("Voice call {} declined by vendor {}", roomName, userEmail);
            
            return new VoiceCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error declining voice call: {}", e.getMessage());
            throw new RuntimeException("Failed to decline voice call", e);
        }
    }

    public List<VoiceCallResponse> getUserCallHistory(String userEmail) {
        List<VoiceCall> calls = voiceCallRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        return calls.stream()
                .map(VoiceCallResponse::new)
                .collect(Collectors.toList());
    }

    public List<VoiceCallResponse> getPendingCallsForVendor(String vendorEmail) {
        List<VoiceCall> calls = voiceCallRepository.findPendingCallsForVendor(vendorEmail);
        return calls.stream()
                .map(VoiceCallResponse::new)
                .collect(Collectors.toList());
    }

    public List<VoiceCallResponse> getPendingCallsForUser(String userEmail) {
        // Get pending calls where the user is the recipient (not the initiator)
        List<VoiceCall> allCalls = voiceCallRepository.findPendingCallsForUser(userEmail);
        
        // Filter out calls where the current user is the buyer (initiator)
        // Only show calls where the user is the vendor (recipient)
        return allCalls.stream()
                .filter(call -> call.getVendorEmail().equals(userEmail)) // Only show calls where user is the recipient
                .map(VoiceCallResponse::new)
                .collect(Collectors.toList());
    }

    private void notifyVendorOfIncomingCall(VoiceCall call, VoiceCallRequest request) {
        try {
            VoiceCallResponse response = new VoiceCallResponse(call);
            
            // Send notification to vendor's personal queue
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/incoming-voice-call",
                Map.of(
                    "call", response,
                    "productName", request.getProductName(),
                    "shopName", request.getShopName(),
                    "type", "INCOMING_VOICE_CALL"
                )
            );
            
            log.info("Notified vendor {} of incoming voice call", call.getVendorEmail());
        } catch (Exception e) {
            log.error("Error notifying vendor of incoming voice call: {}", e.getMessage());
        }
    }

    private void notifyCallStatusUpdate(VoiceCall call) {
        try {
            VoiceCallResponse response = new VoiceCallResponse(call);
            
            // Notify both participants
            messagingTemplate.convertAndSendToUser(
                call.getBuyerEmail(),
                "/queue/voice-call-status",
                Map.of("call", response, "type", "STATUS_UPDATE")
            );
            
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/voice-call-status",
                Map.of("call", response, "type", "STATUS_UPDATE")
            );
            
            log.info("Sent voice call status update for room {}", call.getRoomName());
        } catch (Exception e) {
            log.error("Error sending voice call status update: {}", e.getMessage());
        }
    }

    private void notifyCallEnded(VoiceCall call) {
        try {
            VoiceCallResponse response = new VoiceCallResponse(call);
            
            // Send multiple notifications to ensure both participants get the message
            for (int i = 0; i < 3; i++) {
                // Notify both participants
                messagingTemplate.convertAndSendToUser(
                    call.getBuyerEmail(),
                    "/queue/voice-call-status",
                    Map.of("call", response, "type", "CALL_ENDED", "forceClose", true)
                );
                
                messagingTemplate.convertAndSendToUser(
                    call.getVendorEmail(),
                    "/queue/voice-call-status",
                    Map.of("call", response, "type", "CALL_ENDED", "forceClose", true)
                );
                
                // Small delay between notifications
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            log.info("Notified participants that voice call {} ended", call.getRoomName());
        } catch (Exception e) {
            log.error("Error notifying voice call ended: {}", e.getMessage());
        }
    }

    private void notifyCallDeclined(VoiceCall call) {
        try {
            VoiceCallResponse response = new VoiceCallResponse(call);
            
            // Notify both participants - this will close both modals
            messagingTemplate.convertAndSendToUser(
                call.getBuyerEmail(),
                "/queue/voice-call-status",
                Map.of("call", response, "type", "CALL_DECLINED")
            );
            
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/voice-call-status",
                Map.of("call", response, "type", "CALL_DECLINED")
            );
            
            log.info("Notified both participants that voice call {} was declined", call.getRoomName());
        } catch (Exception e) {
            log.error("Error notifying voice call declined: {}", e.getMessage());
        }
    }

    @Async
    public void scheduleCallTimeout(Long callId, String roomName) {
        CompletableFuture.runAsync(() -> {
            try {
                // Wait 30 seconds
                Thread.sleep(30000);
                
                // Check if call is still pending
                Optional<VoiceCall> callOpt = voiceCallRepository.findById(callId);
                if (callOpt.isPresent()) {
                    VoiceCall call = callOpt.get();
                    
                    // If call is still in INITIATED or RINGING status, mark as MISSED
                    if (call.getStatus() == VoiceCall.CallStatus.INITIATED || 
                        call.getStatus() == VoiceCall.CallStatus.RINGING) {
                        
                        call.setStatus(VoiceCall.CallStatus.MISSED);
                        call.setEndedAt(LocalDateTime.now());
                        voiceCallRepository.save(call);
                        
                        // Notify both participants that call was missed
                        notifyCallMissed(call);
                        
                        log.info("Voice call {} marked as missed after 30 seconds", roomName);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Voice call timeout interrupted for room {}", roomName);
            } catch (Exception e) {
                log.error("Error handling voice call timeout for room {}: {}", roomName, e.getMessage());
            }
        });
    }

    public VoiceCallResponse acceptCall(String roomName) {
        try {
            Optional<VoiceCall> callOpt = voiceCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Voice call not found");
            }

            VoiceCall call = callOpt.get();
            
            // Only accept if call is in INITIATED or RINGING status
            if (call.getStatus() != VoiceCall.CallStatus.INITIATED && 
                call.getStatus() != VoiceCall.CallStatus.RINGING) {
                throw new RuntimeException("Call cannot be accepted in current status: " + call.getStatus());
            }

            call.setStatus(VoiceCall.CallStatus.RINGING);
            VoiceCall savedCall = voiceCallRepository.save(call);

            // Notify both participants about call acceptance
            notifyCallStatusUpdate(call);

            log.info("Voice call {} accepted", roomName);
            
            return new VoiceCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error accepting voice call: {}", e.getMessage());
            throw new RuntimeException("Failed to accept voice call", e);
        }
    }

    public void rejectCall(String roomName) {
        try {
            Optional<VoiceCall> callOpt = voiceCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Voice call not found");
            }

            VoiceCall call = callOpt.get();
            
            // Only reject if call is in INITIATED or RINGING status
            if (call.getStatus() != VoiceCall.CallStatus.INITIATED && 
                call.getStatus() != VoiceCall.CallStatus.RINGING) {
                throw new RuntimeException("Call cannot be rejected in current status: " + call.getStatus());
            }

            call.setStatus(VoiceCall.CallStatus.DECLINED);
            call.setEndedAt(LocalDateTime.now());
            voiceCallRepository.save(call);

            // Notify both participants about call rejection
            notifyCallDeclined(call);

            log.info("Voice call {} rejected", roomName);
        } catch (Exception e) {
            log.error("Error rejecting voice call: {}", e.getMessage());
            throw new RuntimeException("Failed to reject voice call", e);
        }
    }

    private void notifyCallMissed(VoiceCall call) {
        try {
            VoiceCallResponse response = new VoiceCallResponse(call);
            
            // Notify both participants that call was missed - this will close modals
            messagingTemplate.convertAndSendToUser(
                call.getBuyerEmail(),
                "/queue/voice-call-status",
                Map.of("call", response, "type", "CALL_MISSED")
            );
            
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/voice-call-status",
                Map.of("call", response, "type", "CALL_MISSED")
            );
            
            log.info("Notified both participants that voice call {} was missed", call.getRoomName());
        } catch (Exception e) {
            log.error("Error notifying voice call missed: {}", e.getMessage());
        }
    }

    public VoiceCallResponse getCallStatus(String roomName) {
        try {
            Optional<VoiceCall> callOpt = voiceCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Voice call not found");
            }

            VoiceCall call = callOpt.get();
            log.info("Retrieved voice call status for room {}: {}", roomName, call.getStatus());
            
            return new VoiceCallResponse(call);
        } catch (Exception e) {
            log.error("Error getting voice call status: {}", e.getMessage());
            throw new RuntimeException("Failed to get voice call status", e);
        }
    }
}