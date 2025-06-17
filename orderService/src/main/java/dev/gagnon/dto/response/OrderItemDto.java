package dev.gagnon.dto.response;

import dev.gagnon.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String productImage;
    public OrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.productId = orderItem.getProductId();
        this.name = orderItem.getProductName();
        this.unitPrice = orderItem.getUnitPrice();
        this.quantity = orderItem.getQuantity();
        this.productImage = orderItem.getProductImage();

    }
}