package dev.gagnon.dto.response;

import dev.gagnon.dto.request.CartItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String cartId;
    private List<CartItemDto> items;
    private BigDecimal totalAmount;
    private int totalItems;
}