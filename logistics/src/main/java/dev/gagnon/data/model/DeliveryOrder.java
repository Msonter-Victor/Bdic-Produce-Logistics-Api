package dev.gagnon.data.model;

import dev.gagnon.dto.response.DeliveryInfo;
import dev.gagnon.dto.response.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rider_orders")
public class DeliveryOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private Long userId;
    private DeliveryInfo deliveryInfo;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    @OneToOne
    private Rider rider;

}
