package dev.gagnon.service;

import dev.gagnon.dto.VideoCallRequest;
import dev.gagnon.dto.VideoCallResponse;
import dev.gagnon.dto.WebRTCSessionResponse;
import dev.gagnon.model.VideoCall;
import dev.gagnon.repository.VideoCallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebRTCVideoCallService {

    private final VideoCallRepository videoCallRepository;
    private final WebRTCSignalingService signalingService;
    private final SimpMessagingTemplate messagingTemplate;

    public VideoCallResponse initiateCall(VideoCallRequest request) {
        try {
            // Validate that user is not calling themselves
            if (request.getBuyerEmail().equals(request.getVendorEmail())) {
                throw new RuntimeException("Cannot call yourself");
            }

            // Generate unique room name
            String roomName = "webrtc_video_" + UUID.randomUUID().toString().replace("-", "");

            // Create video call record
            VideoCall videoCall = new VideoCall();
            videoCall.setRoomName(roomName);
            videoCall.setBuyerEmail(request.getBuyerEmail());
            videoCall.setVendorEmail(request.getVendorEmail());
            videoCall.setProductId(request.getProductId());
            videoCall.setShopId(request.getShopId());
            videoCall.setStatus(VideoCall.CallStatus.INITIATED);
            videoCall.setCreatedAt(LocalDateTime.now());

            VideoCall savedCall = videoCallRepository.save(videoCall);
            
            // Notify vendor via WebSocket
            notifyVendorOfIncomingCall(savedCall, request);
            
            // Start 45-second timeout for missed call
            scheduleCallTimeout(savedCall.getId(), roomName);
            
            log.info("WebRTC video call initiated: {} between {} and {}", roomName, request.getBuyerEmail(), request.getVendorEmail());
            
            return new VideoCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error initiating WebRTC video call: {}", e.getMessage());
            throw new RuntimeException("Failed to initiate video call", e);
        }
    }

    public WebRTCSessionResponse joinCall(String roomName, String userEmail) {
        try {
            Optional<VideoCall> callOpt = videoCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Video call not found");
            }

            VideoCall call = callOpt.get();
            
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
            if (call.isBuyerJoined() && call.isVendorJoined() && call.getStatus() != VideoCall.CallStatus.ACTIVE) {
                call.setStatus(VideoCall.CallStatus.ACTIVE);
                call.setStartedAt(LocalDateTime.now());
            } else if (call.getStatus() == VideoCall.CallStatus.INITIATED) {
                call.setStatus(VideoCall.CallStatus.RINGING);
            }

            videoCallRepository.save(call);

            // Notify signaling service about user joining
            signalingService.handleUserJoined(roomName, userEmail);

            // Notify other participant about join status
            notifyCallStatusUpdate(call);

            log.info("User {} joined WebRTC video call {}", userEmail, roomName);
            
            // Return WebRTC session info instead of Twilio token
            WebRTCSessionResponse sessionResponse = new WebRTCSessionResponse();
            sessionResponse.setRoomName(roomName);
            sessionResponse.setUserEmail(userEmail);
            sessionResponse.setOtherParticipant(call.getBuyerEmail().equals(userEmail) ? call.getVendorEmail() : call.getBuyerEmail());
            sessionResponse.setCallType("video");
            sessionResponse.setIceServers(getIceServers());
            
            return sessionResponse;
        } catch (Exception e) {
            log.error("Error joining WebRTC video call: {}", e.getMessage());
            throw new RuntimeException("Failed to join video call", e);
        }
    }

    public VideoCallResponse endCall(String roomName, String userEmail) {
        try {
            Optional<VideoCall> callOpt = videoCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Video call not found");
            }

            VideoCall call = callOpt.get();
            
            // Verify user is authorized to end this call
            if (!call.getBuyerEmail().equals(userEmail) && !call.getVendorEmail().equals(userEmail)) {
                throw new RuntimeException("Unauthorized to end this call");
            }

            // Only end if not already ended, declined, or missed
            if (call.getStatus() == VideoCall.CallStatus.ENDED || 
                call.getStatus() == VideoCall.CallStatus.DECLINED || 
                call.getStatus() == VideoCall.CallStatus.MISSED) {
                return new VideoCallResponse(call);
            }

            // Calculate duration if call was active
            if (call.getStartedAt() != null) {
                long duration = java.time.Duration.between(call.getStartedAt(), LocalDateTime.now()).getSeconds();
                call.setDurationSeconds(duration);
            }

            call.setStatus(VideoCall.CallStatus.ENDED);
            call.setEndedAt(LocalDateTime.now());

            VideoCall savedCall = videoCallRepository.save(call);

            // Notify signaling service about user leaving
            signalingService.handleUserLeft(roomName, userEmail);

            // Notify both participants about call end
            notifyCallEnded(call);

            log.info("WebRTC video call {} ended by {}", roomName, userEmail);
            
            return new VideoCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error ending WebRTC video call: {}", e.getMessage());
            throw new RuntimeException("Failed to end video call", e);
        }
    }

    public VideoCallResponse declineCall(String roomName, String userEmail) {
        try {
            Optional<VideoCall> callOpt = videoCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Video call not found");
            }

            VideoCall call = callOpt.get();
            
            // Only vendor can decline
            if (!call.getVendorEmail().equals(userEmail)) {
                throw new RuntimeException("Only vendor can decline the call");
            }

            // Only decline if not already ended, declined, or missed
            if (call.getStatus() == VideoCall.CallStatus.ENDED || 
                call.getStatus() == VideoCall.CallStatus.DECLINED || 
                call.getStatus() == VideoCall.CallStatus.MISSED) {
                return new VideoCallResponse(call);
            }

            call.setStatus(VideoCall.CallStatus.DECLINED);
            call.setEndedAt(LocalDateTime.now());

            VideoCall savedCall = videoCallRepository.save(call);

            // Notify both participants about call decline
            notifyCallDeclined(call);

            log.info("WebRTC video call {} declined by vendor {}", roomName, userEmail);
            
            return new VideoCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error declining WebRTC video call: {}", e.getMessage());
            throw new RuntimeException("Failed to decline video call", e);
        }
    }

    public VideoCallResponse acceptCall(String roomName) {
        try {
            Optional<VideoCall> callOpt = videoCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Video call not found");
            }

            VideoCall call = callOpt.get();
            
            // Only accept if call is in INITIATED or RINGING status
            if (call.getStatus() != VideoCall.CallStatus.INITIATED && 
                call.getStatus() != VideoCall.CallStatus.RINGING) {
                throw new RuntimeException("Call cannot be accepted in current status: " + call.getStatus());
            }

            call.setStatus(VideoCall.CallStatus.RINGING);
            VideoCall savedCall = videoCallRepository.save(call);

            // Notify both participants about call acceptance
            notifyCallStatusUpdate(call);

            log.info("WebRTC video call {} accepted", roomName);
            
            return new VideoCallResponse(savedCall);
        } catch (Exception e) {
            log.error("Error accepting WebRTC video call: {}", e.getMessage());
            throw new RuntimeException("Failed to accept video call", e);
        }
    }

    public List<VideoCallResponse> getUserCallHistory(String userEmail) {
        List<VideoCall> calls = videoCallRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        return calls.stream()
                .map(VideoCallResponse::new)
                .collect(Collectors.toList());
    }

    public List<VideoCallResponse> getPendingCallsForVendor(String vendorEmail) {
        List<VideoCall> calls = videoCallRepository.findPendingCallsForVendor(vendorEmail);
        return calls.stream()
                .map(VideoCallResponse::new)
                .collect(Collectors.toList());
    }

    public VideoCallResponse getCallStatus(String roomName) {
        try {
            Optional<VideoCall> callOpt = videoCallRepository.findByRoomName(roomName);
            if (callOpt.isEmpty()) {
                throw new RuntimeException("Video call not found");
            }

            VideoCall call = callOpt.get();
            log.info("Retrieved WebRTC video call status for room {}: {}", roomName, call.getStatus());
            
            return new VideoCallResponse(call);
        } catch (Exception e) {
            log.error("Error getting WebRTC video call status: {}", e.getMessage());
            throw new RuntimeException("Failed to get video call status", e);
        }
    }

    private void notifyVendorOfIncomingCall(VideoCall call, VideoCallRequest request) {
        try {
            VideoCallResponse response = new VideoCallResponse(call);
            
            // Send notification to vendor's personal queue
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/incoming-call",
                Map.of(
                    "call", response,
                    "productName", request.getProductName(),
                    "shopName", request.getShopName(),
                    "type", "INCOMING_CALL",
                    "callType", "webrtc-video"
                )
            );
            
            log.info("Notified vendor {} of incoming WebRTC video call", call.getVendorEmail());
        } catch (Exception e) {
            log.error("Error notifying vendor of incoming WebRTC video call: {}", e.getMessage());
        }
    }

    private void notifyCallStatusUpdate(VideoCall call) {
        try {
            VideoCallResponse response = new VideoCallResponse(call);
            
            // Notify both participants
            messagingTemplate.convertAndSendToUser(
                call.getBuyerEmail(),
                "/queue/call-status",
                Map.of("call", response, "type", "STATUS_UPDATE", "callType", "webrtc-video")
            );
            
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/call-status",
                Map.of("call", response, "type", "STATUS_UPDATE", "callType", "webrtc-video")
            );
            
            log.info("Sent WebRTC video call status update for room {}", call.getRoomName());
        } catch (Exception e) {
            log.error("Error sending WebRTC video call status update: {}", e.getMessage());
        }
    }

    private void notifyCallEnded(VideoCall call) {
        try {
            VideoCallResponse response = new VideoCallResponse(call);
            
            // Send multiple notifications to ensure both participants get the message
            for (int i = 0; i < 3; i++) {
                // Notify both participants
                messagingTemplate.convertAndSendToUser(
                    call.getBuyerEmail(),
                    "/queue/call-status",
                    Map.of("call", response, "type", "CALL_ENDED", "forceClose", true, "callType", "webrtc-video")
                );
                
                messagingTemplate.convertAndSendToUser(
                    call.getVendorEmail(),
                    "/queue/call-status",
                    Map.of("call", response, "type", "CALL_ENDED", "forceClose", true, "callType", "webrtc-video")
                );
                
                // Small delay between notifications
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            log.info("Notified participants that WebRTC video call {} ended", call.getRoomName());
        } catch (Exception e) {
            log.error("Error notifying WebRTC video call ended: {}", e.getMessage());
        }
    }

    private void notifyCallDeclined(VideoCall call) {
        try {
            VideoCallResponse response = new VideoCallResponse(call);
            
            // Notify both participants
            messagingTemplate.convertAndSendToUser(
                call.getBuyerEmail(),
                "/queue/call-status",
                Map.of("call", response, "type", "CALL_DECLINED", "callType", "webrtc-video")
            );
            
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/call-status",
                Map.of("call", response, "type", "CALL_DECLINED", "callType", "webrtc-video")
            );
            
            log.info("Notified both participants that WebRTC video call {} was declined", call.getRoomName());
        } catch (Exception e) {
            log.error("Error notifying WebRTC video call declined: {}", e.getMessage());
        }
    }

    @Async
    public void scheduleCallTimeout(Long callId, String roomName) {
        CompletableFuture.runAsync(() -> {
            try {
                // Wait 45 seconds
                Thread.sleep(45000);
                
                // Check if call is still pending
                Optional<VideoCall> callOpt = videoCallRepository.findById(callId);
                if (callOpt.isPresent()) {
                    VideoCall call = callOpt.get();
                    
                    // If call is still in INITIATED or RINGING status, mark as MISSED
                    if (call.getStatus() == VideoCall.CallStatus.INITIATED || 
                        call.getStatus() == VideoCall.CallStatus.RINGING) {
                        
                        call.setStatus(VideoCall.CallStatus.MISSED);
                        call.setEndedAt(LocalDateTime.now());
                        videoCallRepository.save(call);
                        
                        // Notify both participants that call was missed
                        notifyCallMissed(call);
                        
                        log.info("WebRTC video call {} marked as missed after 45 seconds", roomName);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("WebRTC video call timeout interrupted for room {}", roomName);
            } catch (Exception e) {
                log.error("Error handling WebRTC video call timeout for room {}: {}", roomName, e.getMessage());
            }
        });
    }

    private void notifyCallMissed(VideoCall call) {
        try {
            VideoCallResponse response = new VideoCallResponse(call);
            
            // Notify both participants that call was missed
            messagingTemplate.convertAndSendToUser(
                call.getBuyerEmail(),
                "/queue/call-status",
                Map.of("call", response, "type", "CALL_MISSED", "callType", "webrtc-video")
            );
            
            messagingTemplate.convertAndSendToUser(
                call.getVendorEmail(),
                "/queue/call-status",
                Map.of("call", response, "type", "CALL_MISSED", "callType", "webrtc-video")
            );
            
            log.info("Notified both participants that WebRTC video call {} was missed", call.getRoomName());
        } catch (Exception e) {
            log.error("Error notifying WebRTC video call missed: {}", e.getMessage());
        }
    }

    private final WebRTCConfig webRTCConfig;

    private List<Map<String, String>> getIceServers() {
        return webRTCConfig.getIceServersAsMap();
    }
}