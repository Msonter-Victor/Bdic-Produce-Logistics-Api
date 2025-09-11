package dev.gagnon.controller;

import dev.gagnon.dto.VideoCallRequest;
import dev.gagnon.dto.VideoCallResponse;
import dev.gagnon.dto.TwilioTokenResponse;
import dev.gagnon.service.VideoCallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video-calls")
@RequiredArgsConstructor
@Slf4j
public class VideoCallController {

    private final VideoCallService videoCallService;

    @PostMapping("/initiate")
    public ResponseEntity<VideoCallResponse> initiateCall(@RequestBody VideoCallRequest request) {
        try {
            // Validate that user is not calling themselves
            if (request.getBuyerEmail().equals(request.getVendorEmail())) {
                log.warn("User {} attempted to call themselves", request.getBuyerEmail());
                return ResponseEntity.badRequest().build();
            }

            VideoCallResponse response = videoCallService.initiateCall(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error initiating video call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/join/{roomName}")
    public ResponseEntity<TwilioTokenResponse> joinCall(
            @PathVariable String roomName,
            @RequestParam String userEmail) {
        try {
            TwilioTokenResponse response = videoCallService.joinCall(roomName, userEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error joining video call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/end/{roomName}")
    public ResponseEntity<VideoCallResponse> endCall(
            @PathVariable String roomName,
            @RequestParam String userEmail) {
        try {
            VideoCallResponse response = videoCallService.endCall(roomName, userEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error ending video call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/decline/{roomName}")
    public ResponseEntity<VideoCallResponse> declineCall(
            @PathVariable String roomName,
            @RequestParam String userEmail) {
        try {
            VideoCallResponse response = videoCallService.declineCall(roomName, userEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error declining video call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<VideoCallResponse>> getCallHistory(@RequestParam String userEmail) {
        try {
            List<VideoCallResponse> history = videoCallService.getUserCallHistory(userEmail);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error getting call history: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<VideoCallResponse>> getPendingCalls(@RequestParam String vendorEmail) {
        try {
            List<VideoCallResponse> pendingCalls = videoCallService.getPendingCallsForVendor(vendorEmail);
            return ResponseEntity.ok(pendingCalls);
        } catch (Exception e) {
            log.error("Error getting pending calls: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accept/{roomName}")
    public ResponseEntity<VideoCallResponse> acceptCall(@PathVariable String roomName) {
        try {
            VideoCallResponse response = videoCallService.acceptCall(roomName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error accepting video call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}