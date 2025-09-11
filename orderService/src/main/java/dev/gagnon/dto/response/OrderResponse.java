package dev.gagnon.dto.response;

import dev.gagnon.model.DeliveryInfo;
import dev.gagnon.model.constants.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String buyerEmail;
    private OrderStatus status;
    private DeliveryInfo deliveryInfo;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
    private boolean isParentOrder;
    private Long shopId;
    private int shopOrdersCount;


}

