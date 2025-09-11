package dev.gagnon.controller;

import dev.gagnon.dto.CallRequest;
import dev.gagnon.dto.CallResponse;
import dev.gagnon.model.Call;
import dev.gagnon.service.TwilioCallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
@Slf4j
public class CallController {

    private final TwilioCallService callService;

    @PostMapping("/initiate")
    public ResponseEntity<CallResponse> initiateCall(@RequestBody CallRequest request) {
        try {
            CallResponse response = callService.initiateCall(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to initiate call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{callId}/answer")
    public ResponseEntity<CallResponse> answerCall(
            @PathVariable Long callId,
            @RequestParam String vendorEmail) {
        try {
            CallResponse response = callService.answerCall(callId, vendorEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to answer call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CallResponse>> getPendingCalls(@RequestParam String vendorEmail) {
        try {
            List<CallResponse> pendingCalls = callService.getPendingCalls(vendorEmail);
            return ResponseEntity.ok(pendingCalls);
        } catch (Exception e) {
            log.error("Failed to get pending calls: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{callId}/reject")
    public ResponseEntity<Void> rejectCall(
            @PathVariable Long callId,
            @RequestParam String vendorEmail) {
        try {
            callService.rejectCall(callId, vendorEmail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to reject call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{callId}/end")
    public ResponseEntity<Void> endCall(
            @PathVariable Long callId,
            @RequestParam String userEmail) {
        try {
            callService.endCall(callId, userEmail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to end call: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<CallResponse>> getCallHistory(@RequestParam String userEmail) {
        try {
            List<CallResponse> calls = callService.getCallHistory(userEmail);
            return ResponseEntity.ok(calls);
        } catch (Exception e) {
            log.error("Failed to get call history: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/vendor")
    public ResponseEntity<List<CallResponse>> getVendorCalls(
            @RequestParam String vendorEmail,
            @RequestParam(required = false) Call.CallStatus status) {
        try {
            List<CallResponse> calls = callService.getVendorCalls(vendorEmail, status);
            return ResponseEntity.ok(calls);
        } catch (Exception e) {
            log.error("Failed to get vendor calls: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}