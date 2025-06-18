package dev.gagnon.controller;

import dev.gagnon.dto.request.AddToWishlistRequest;
import dev.gagnon.dto.request.GetUserOrdersRequest;
import dev.gagnon.dto.response.AddToWishlistResponse;
import dev.gagnon.dto.response.OrderResponse;
import dev.gagnon.dto.response.WishListResponse;
import dev.gagnon.model.OrderStatus;
import dev.gagnon.model.WishListItem;
import dev.gagnon.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@RequestParam String buyerEmail) {
        List<OrderResponse> orders = orderService.getUserOrders(buyerEmail);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable String orderNumber,
            Principal principal) {
        String buyerEmail = principal.getName();
        OrderResponse order = orderService.getOrder(orderNumber, buyerEmail);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderNumber}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(orderNumber, status);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse>allOrders = orderService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    @PostMapping("/add-to-wishlist")
    public ResponseEntity<?> addToWishlist(@RequestBody AddToWishlistRequest request){
        AddToWishlistResponse response = orderService.addToWishList(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-wishlist")
    public ResponseEntity<?> getUserWishlist(@RequestParam String buyerEmail) {
        WishListResponse wishList = orderService.getUserWishList(buyerEmail);
        return ResponseEntity.ok(wishList);
    }
}