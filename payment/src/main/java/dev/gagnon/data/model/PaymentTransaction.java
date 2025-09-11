package dev.gagnon.data.model;

import dev.gagnon.data.constants.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private String email;
    private Integer amount;
    private String currency;
    private String accessCode;
    private String status;
    private String callBackUrl;
    private String authorizationUrl;
    private PaymentType paymentType;
    private String payerName = "";
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}