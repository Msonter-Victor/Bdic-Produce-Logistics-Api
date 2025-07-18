package dev.gagnon.controller;

import dev.gagnon.dto.request.AddToCartRequest;
import dev.gagnon.dto.request.CheckoutRequest;
import dev.gagnon.dto.request.UpdateCartItemRequest;
import dev.gagnon.dto.response.CartResponse;
import dev.gagnon.dto.response.OrderResponse;
import dev.gagnon.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader(value = "X-Cart-Id", required = false) String cartId,
            @Valid @RequestBody AddToCartRequest request,
            Principal principal) {
        String buyerEmail = principal != null ? principal.getName() : null;
        CartResponse response = orderService.addToCart(cartId, buyerEmail, request);
        return ResponseEntity.ok()
                .header("X-Cart-Id", response.getCartId())
                .body(response);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader(value = "X-Cart-Id", required = false) String cartId,
            Principal principal) {
        String buyerEmail = principal != null ? principal.getName() : null;
        CartResponse response = orderService.getCart(cartId, buyerEmail);
        return ResponseEntity.ok()
                .header("X-Cart-Id", response.getCartId())
                .body(response);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @RequestHeader("X-Cart-Id") String cartId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Principal principal) {
        String buyerEmail = principal != null ? principal.getName() : null;
        CartResponse response = orderService.updateCartItem(cartId, buyerEmail, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @RequestHeader("X-Cart-Id") String cartId,
            @PathVariable Long itemId,
            Principal principal) {
        String buyerEmail = principal != null ? principal.getName() : null;
        CartResponse response = orderService.removeFromCart(cartId, buyerEmail, itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-Cart-Id") String cartId,
            Principal principal) {
        String buyerEmail = principal != null ? principal.getName() : null;
        orderService.clearCart(cartId, buyerEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestHeader("X-Cart-Id") String cartId,
            @Valid @RequestBody CheckoutRequest request) {
        OrderResponse response = orderService.checkout(cartId, request);
        return ResponseEntity.ok(response);
    }
}