package dev.gagnon.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gagnon.dto.response.DeliveryInfo;
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
@Table(name = "rider_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private String customerName;
    @OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true, fetch=FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private List<OrderItem> items = new ArrayList();
    @Embedded
    private DeliveryInfo deliveryInfo;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private BigDecimal grandTotal;
    private BigDecimal totalAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}