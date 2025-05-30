package dev.gagnon.controller;

import dev.gagnon.dto.request.AddToCartRequest;
import dev.gagnon.dto.request.CheckoutRequest;
import dev.gagnon.dto.request.UpdateCartItemRequest;
import dev.gagnon.dto.response.CartResponse;
import dev.gagnon.dto.response.OrderResponse;
import dev.gagnon.model.OrderStatus;
import dev.gagnon.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    // CART ENDPOINTS
    @PostMapping("/cart")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request) {
        
        CartResponse response = orderService.addToCart(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cart")
    public ResponseEntity<CartResponse> getCart(@RequestHeader("User-Id") Long userId) {
        CartResponse response = orderService.getCart(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/cart/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        
        CartResponse response = orderService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long itemId) {
        
        CartResponse response = orderService.removeFromCart(userId, itemId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/cart")
    public ResponseEntity<Void> clearCart(@RequestHeader("User-Id") Long userId) {
        orderService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
    
    // ORDER ENDPOINTS
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody CheckoutRequest request) {
        
        OrderResponse response = orderService.checkout(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(@RequestHeader("User-Id") Long userId) {
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestHeader("User-Id") Long userId,
            @PathVariable String orderNumber) {
        
        OrderResponse order = orderService.getOrder(userId, orderNumber);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderNumber}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        
        OrderResponse order = orderService.updateOrderStatus(orderNumber, status);
        return ResponseEntity.ok(order);
    }
}
