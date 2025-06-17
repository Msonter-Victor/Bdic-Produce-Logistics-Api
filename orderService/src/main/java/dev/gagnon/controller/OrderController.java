package dev.gagnon.controller;

import dev.gagnon.dto.response.OrderResponse;
import dev.gagnon.model.OrderStatus;
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

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(Principal principal) {
        String buyerEmail = principal.getName();
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
}