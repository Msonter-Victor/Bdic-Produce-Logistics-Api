package dev.gagnon.controller;

import dev.gagnon.dto.VoiceCallRequest;
import dev.gagnon.dto.VoiceCallResponse;
import dev.gagnon.dto.TwilioTokenResponse;
import dev.gagnon.service.VoiceCallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voice-calls")
@RequiredArgsConstructor
@Slf4j
public class VoiceCallController {

    private final VoiceCallService voiceCallService;

    @PostMapping("/initiate")
    public ResponseEntity<VoiceCallResponse> initiateCall(@RequestBody VoiceCallRequest request) {
        try {
            // Validate that user is not calling themselves
            if (request.getBuyerEmail().equals(request.getVendorEmail())) {
                log.warn("User {} attempted to call themselves", request.getBuyerEmail());
                return ResponseEntity.badRequest().build();
            }

            VoiceCallResponse response = voiceCallService.initiateCall(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error initiating voice call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/join/{roomName}")
    public ResponseEntity<TwilioTokenResponse> joinCall(
            @PathVariable String roomName,
            @RequestParam String userEmail) {
        try {
            TwilioTokenResponse response = voiceCallService.joinCall(roomName, userEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error joining voice call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/end/{roomName}")
    public ResponseEntity<VoiceCallResponse> endCall(
            @PathVariable String roomName,
            @RequestParam String userEmail) {
        try {
            VoiceCallResponse response = voiceCallService.endCall(roomName, userEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error ending voice call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/decline/{roomName}")
    public ResponseEntity<VoiceCallResponse> declineCall(
            @PathVariable String roomName,
            @RequestParam String userEmail) {
        try {
            VoiceCallResponse response = voiceCallService.declineCall(roomName, userEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error declining voice call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<VoiceCallResponse>> getCallHistory(@RequestParam String userEmail) {
        try {
            List<VoiceCallResponse> history = voiceCallService.getUserCallHistory(userEmail);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error getting voice call history: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<VoiceCallResponse>> getPendingCalls(@RequestParam String vendorEmail) {
        try {
            List<VoiceCallResponse> pendingCalls = voiceCallService.getPendingCallsForVendor(vendorEmail);
            return ResponseEntity.ok(pendingCalls);
        } catch (Exception e) {
            log.error("Error getting pending voice calls: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending-user")
    public ResponseEntity<List<VoiceCallResponse>> getPendingCallsForUser(@RequestParam String userEmail) {
        try {
            List<VoiceCallResponse> pendingCalls = voiceCallService.getPendingCallsForUser(userEmail);
            return ResponseEntity.ok(pendingCalls);
        } catch (Exception e) {
            log.error("Error getting pending voice calls for user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accept/{roomName}")
    public ResponseEntity<VoiceCallResponse> acceptCall(@PathVariable String roomName) {
        try {
            VoiceCallResponse response = voiceCallService.acceptCall(roomName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error accepting voice call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reject/{roomName}")
    public ResponseEntity<String> rejectCall(@PathVariable String roomName) {
        try {
            voiceCallService.rejectCall(roomName);
            return ResponseEntity.ok("Call rejected successfully");
        } catch (Exception e) {
            log.error("Error rejecting voice call: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error rejecting call: " + e.getMessage());
        }
    }
}