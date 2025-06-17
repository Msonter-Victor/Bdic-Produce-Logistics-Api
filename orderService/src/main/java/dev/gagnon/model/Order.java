package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private String buyerEmail;
    @Embedded
    private DeliveryInfo deliveryInfo;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private BigDecimal grandTotal;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
