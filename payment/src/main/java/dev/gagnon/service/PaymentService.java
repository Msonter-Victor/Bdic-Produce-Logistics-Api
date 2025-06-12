// PaymentService.java
package dev.gagnon.Service;

import dev.gagnon.data.model.PaymentTransaction;
import dev.gagnon.data.repository.PaymentTransactionRepository;
import dev.gagnon.dto.request.InitializePaymentRequest;
import dev.gagnon.dto.response.InitializePaymentResponse;
import dev.gagnon.dto.response.VerifyPaymentResponse;
import dev.gagnon.Exception.PaymentException;
import dev.gagnon.utils.CredoApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CredoApiClient credoApiClient;
    private final PaymentTransactionRepository paymentTransactionRepository;
    
    @Transactional
    public InitializePaymentResponse initializePayment(InitializePaymentRequest request) {
        // Call Credo API
        InitializePaymentResponse response = credoApiClient.initializePayment(request);

        if (!"200".equals(response.getStatus())) {
            throw new PaymentException("Failed to initialize payment: " +
                (response.getErrorMessage() != null ? response.getErrorMessage() : "Unknown error"));
        }
        
        // Save transaction to database
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setReference(response.getData().getReference());
        transaction.setCredoReference(response.getData().getCredoReference());
        transaction.setEmail(request.getEmail());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency() != null ? request.getCurrency() : "NGN");
        transaction.setCallbackUrl(request.getCallbackUrl());
        transaction.setAuthorizationUrl(response.getData().getAuthorizationUrl());
        transaction.setStatus("PENDING");
        
        paymentTransactionRepository.save(transaction);
        
        return response;
    }
    
    @Transactional
    public VerifyPaymentResponse verifyPayment(String transRef) {
        // Call Credo API to verify payment
        VerifyPaymentResponse response = credoApiClient.verifyPayment(transRef);
        
        if (!"200".equals(response.getStatus())) {
            throw new PaymentException("Failed to verify payment: " + 
                (response.getErrorMessage() != null ? response.getErrorMessage() : "Unknown error"));
        }
        
        // Update transaction status in database
        paymentTransactionRepository.findByCredoReference(transRef)
            .ifPresent(transaction -> {
                transaction.setStatus(response.getData().getStatus().toString());
                paymentTransactionRepository.save(transaction);
            });
        
        return response;
    }
    
    public void handlePaymentCallback(String status, String errorMessage, String transRef, 
                                    Double transAmount, String currency, String reference) {
        PaymentTransaction transaction = paymentTransactionRepository.findByCredoReference(transRef)
            .orElseThrow(() -> new PaymentException("Transaction not found"));
        
        // Update transaction status based on callback
        transaction.setStatus(status);
        paymentTransactionRepository.save(transaction);
    }
}