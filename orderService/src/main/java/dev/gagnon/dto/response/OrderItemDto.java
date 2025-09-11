package dev.gagnon.dto.response;

import dev.gagnon.model.OrderItem;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private String description;
    private String productImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String vendorName;
    private String buyerName;
    private String itemStatus;

    public OrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.productId = orderItem.getProductId();
        this.productName = orderItem.getProductName();
        this.description = orderItem.getDescription();
        this.productImage = orderItem.getProductImage();
        this.quantity = orderItem.getQuantity();
        this.unitPrice = orderItem.getUnitPrice();
        this.itemStatus = String.valueOf(orderItem.getStatus());
        this.totalPrice = orderItem.getUnitPrice() != null && orderItem.getQuantity() != null 
            ? orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity().intValue()))
            : BigDecimal.ZERO;
        try {
            if (orderItem.getOrder() != null && 
                orderItem.getOrder().getShop() != null && 
                orderItem.getOrder().getShop().getUser() != null) {
                this.vendorName = orderItem.getOrder().getShop().getUser().getFirstName() + " " + 
                                orderItem.getOrder().getShop().getUser().getLastName();
            } else {
                this.vendorName = "Unknown Vendor";
            }
        } catch (Exception e) {
            this.vendorName = "Unknown Vendor";
        }
    }

}

