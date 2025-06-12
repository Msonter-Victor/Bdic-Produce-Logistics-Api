package dev.gagnon.DTO;

import lombok.Data;

@Data
public class CartItemRequestDTO {
    private Long productId;
    private Integer quantity;
}
