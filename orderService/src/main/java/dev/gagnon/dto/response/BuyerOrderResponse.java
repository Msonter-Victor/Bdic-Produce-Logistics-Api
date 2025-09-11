package dev.gagnon.dto.response;

import dev.gagnon.model.DeliveryInfo;
import dev.gagnon.model.constants.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerOrderResponse {
    private Long id;
    private String orderNumber;
    private String status;
    private DeliveryInfo deliveryInfo;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}

