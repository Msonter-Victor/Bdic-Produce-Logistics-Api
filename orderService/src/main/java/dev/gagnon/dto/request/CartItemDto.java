package dev.gagnon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
    
    // getters and setters
}