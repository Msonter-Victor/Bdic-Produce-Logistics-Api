package dev.gagnon.dto.response;

import dev.gagnon.model.DeliveryInfo;
import dev.gagnon.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private DeliveryInfo deliveryInfo;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}
