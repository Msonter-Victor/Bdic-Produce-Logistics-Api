package dev.gagnon.Service;

import dev.gagnon.DTO.CartDTO;
import dev.gagnon.DTO.CartItemRequestDTO;

import java.math.BigDecimal;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addOrUpdateCartItem(Long userId, CartItemRequestDTO request);
    CartDTO removeCartItem(Long userId, Long productId);
    BigDecimal calculateCartTotal(Long userId);

}
