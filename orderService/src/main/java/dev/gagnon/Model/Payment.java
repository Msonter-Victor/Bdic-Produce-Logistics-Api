package dev.gagnon.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String reference;
    private String credoReference;
    private String email;
    private Integer amount;
    private String currency;
    private String status;
    private String callbackUrl;
    private String authorizationUrl;

    // In Payment.java
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}