// PaymentService.java
package dev.gagnon.service;

import dev.gagnon.data.constants.PaymentType;
import dev.gagnon.data.model.PaymentTransaction;
import dev.gagnon.data.repository.PaymentTransactionRepository;
import dev.gagnon.dto.request.InitializePaymentRequest;
import dev.gagnon.dto.request.TransferRequest;
import dev.gagnon.dto.response.*;
import dev.gagnon.execption.PaymentException;
import dev.gagnon.utils.PaystackApiClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.gagnon.data.constants.PaymentType.SHOP_PROMOTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaystackApiClient paystackApiClient; // Changed from CredoApiClient
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ServiceClient serviceClient;

    @Transactional
    public InitializePaymentResponse initializePayment(InitializePaymentRequest request) {
        InitializePaymentResponse response = paystackApiClient.initializePayment(request);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPaymentType(PaymentType.valueOf(request.getPaymentType()));
        transaction.setReference(response.getData().getReference());
        transaction.setCallBackUrl(request.getCallbackUrl());
        transaction.setEmail(request.getEmail());
        transaction.setAmount(request.getAmount());
        transaction.setPaymentType(PaymentType.valueOf(request.getPaymentType()));
        transaction.setCurrency(request.getCurrency() != null ? request.getCurrency() : "NGN");
        transaction.setAuthorizationUrl(response.getData().getAuthorizationUrl());
        transaction.setStatus("pending");
        transaction.setAccessCode(response.getData().getAccessCode());
        paymentTransactionRepository.save(transaction);

        return response;
    }

    @Transactional
    public VerifyPaymentResponse verifyPayment(String reference) {
        VerifyPaymentResponse response = paystackApiClient.verifyPayment(reference);
        log.info("Verify response 11: {}", response);
        if (!"true".equalsIgnoreCase(String.valueOf(response.getStatus()))) {
            throw new PaymentException("Failed to verify payment" );
        }
        log.info("Verify response : {}", response);
        paymentTransactionRepository.findByReference(reference)
                .ifPresent(transaction -> {
                    transaction.setStatus("success");
                    paymentTransactionRepository.save(transaction);
                });
        return response;
    }

    public void handlePaymentCallback(String status, String errorMessage, String transRef, Double transAmount, String currency, String reference) {
            PaymentTransaction transaction = paymentTransactionRepository.findByReference(transRef)
                    .orElseThrow(() -> new PaymentException("Transaction not found"));

            transaction.setStatus(status);
            paymentTransactionRepository.save(transaction);
    }


    public Double getTotalTransactionAmount() {
        List<PaymentTransaction> transactions = paymentTransactionRepository.findAll();
        return transactions
                .stream().mapToDouble(PaymentTransaction::getAmount)
                .sum();
    }

    public List<PaymentTransactionResponse> getAllPromotionsTransactions() {
        List<PaymentTransaction> transactions = paymentTransactionRepository.findByPaymentType(SHOP_PROMOTION);
        return transactions.stream()
                .map(this::mapToResponse)
                .toList();
    }



    private PaymentTransactionResponse mapToResponse(PaymentTransaction paymentTransaction) {
        PaymentTransactionResponse response = new PaymentTransactionResponse();
        response.setId(paymentTransaction.getId());
        response.setAmount(paymentTransaction.getAmount());
        response.setReference(paymentTransaction.getReference());
        response.setEmail(paymentTransaction.getEmail());
        response.setStatus(paymentTransaction.getStatus());
        response.setCreatedAt(paymentTransaction.getCreatedAt());
        response.setPaymentType(String.valueOf(paymentTransaction.getPaymentType()));
        return response;
    }

    public List<PaymentTransactionResponse> getTotalTransactions() {
        List<PaymentTransaction>transactions = paymentTransactionRepository.findAll();
        return transactions.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PaymentTransactionResponse getTransaction(Long id) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new PaymentException("Transaction not found"));
        return mapToResponse(transaction);
    }

    public Double getTotalPromotionAmount() {
        List<PaymentTransaction> transactions = paymentTransactionRepository.findByPaymentType(SHOP_PROMOTION);
        return transactions.stream()
                .mapToDouble(PaymentTransaction::getAmount)
                .sum();
    }

    public BankListResponse getBanks() {
        return paystackApiClient.getBanks();
    }
    public AccountValidationResponse validateAccountNumber(String accountNumber, String bankCode) {
        return paystackApiClient.validateAccountNumber(accountNumber, bankCode);
    }

    public TransferResponse transfer(TransferRequest request) {
        String reference = "TRF-" + UUID.randomUUID().toString().replaceAll("[^a-z0-9_-]", "").substring(0, 16);
        String recipientCode = paystackApiClient.createRecipient(request).getData().getRecipientCode();
        return paystackApiClient.initiateTransfer(recipientCode,request.getAmount(),"payment",reference);
    }
}