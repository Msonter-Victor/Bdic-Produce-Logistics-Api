package dev.gagnon.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal discountAmount;
    private Integer quantity;
    private BigDecimal subtotal;
    private Long statusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
