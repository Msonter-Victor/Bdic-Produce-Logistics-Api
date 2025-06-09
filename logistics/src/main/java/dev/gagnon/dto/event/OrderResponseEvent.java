package dev.gagnon.dto.event;

import dev.gagnon.dto.response.DeliveryInfo;
import dev.gagnon.dto.response.OrderItemResponse;
import dev.gagnon.dto.response.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseEvent {
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
    private String correlationId;

}
