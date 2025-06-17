package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private List<OrderItemResponse> items;
    private DeliveryInfo deliveryInfo;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
    private OrderStatus status;
    private LocalDateTime createdAt;


}