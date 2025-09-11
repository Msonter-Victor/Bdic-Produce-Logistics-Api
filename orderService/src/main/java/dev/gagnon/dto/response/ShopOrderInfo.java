package dev.gagnon.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderInfo {
    private Long shopId;
    private String shopName;
    private String vendorName;
    private String vendorEmail;
    private BigDecimal subtotal;
    private List<OrderItemDto> items;
}