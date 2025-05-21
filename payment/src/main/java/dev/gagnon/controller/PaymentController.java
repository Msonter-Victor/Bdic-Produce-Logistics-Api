// PaymentController.java
package dev.gagnon.controller;

import dev.gagnon.dto.request.InitializePaymentRequest;
import dev.gagnon.dto.response.InitializePaymentResponse;
import dev.gagnon.dto.response.VerifyPaymentResponse;
import dev.gagnon.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    
    @PostMapping("/initialize")
    public ResponseEntity<InitializePaymentResponse> initializePayment(
            @Valid @RequestBody InitializePaymentRequest request) {
        InitializePaymentResponse response = paymentService.initializePayment(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/verify/{transRef}")
    public ResponseEntity<VerifyPaymentResponse> verifyPayment(
            @PathVariable String transRef) {
        VerifyPaymentResponse response = paymentService.verifyPayment(transRef);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/callback")
    public ResponseEntity<String> paymentCallback(
            @RequestParam String status,
            @RequestParam String errorMessage,
            @RequestParam String transRef,
            @RequestParam Double transAmount,
            @RequestParam String currency,
            @RequestParam String reference) {
        
        paymentService.handlePaymentCallback(status, errorMessage, transRef, transAmount, currency, reference);
        return ResponseEntity.ok("Callback processed successfully");
    }
}