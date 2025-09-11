/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.controller.CartController
 *  dev.gagnon.dto.request.AddToCartRequest
 *  dev.gagnon.dto.request.CheckoutRequest
 *  dev.gagnon.dto.request.UpdateCartItemRequest
 *  dev.gagnon.dto.response.CartResponse
 *  dev.gagnon.dto.response.OrderResponse
 *  dev.gagnon.exception.OrderBaseException
 *  dev.gagnon.service.OrderService
 *  jakarta.validation.Valid
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.ResponseEntity$BodyBuilder
 *  org.springframework.web.bind.annotation.DeleteMapping
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.PutMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package dev.gagnon.controller;

import dev.gagnon.dto.request.AddToCartRequest;
import dev.gagnon.dto.request.CheckoutRequest;
import dev.gagnon.dto.request.UpdateCartItemRequest;
import dev.gagnon.dto.response.CartResponse;
import dev.gagnon.dto.response.OrderResponse;
import dev.gagnon.exception.OrderBaseException;
import dev.gagnon.service.OrderService;
import jakarta.validation.Valid;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value={"/add"})
    public ResponseEntity<?> addToCart(@RequestHeader(value="X-Cart-Id", required=false) String cartId, @Valid @RequestBody AddToCartRequest request, Principal principal) {
        String buyerEmail = principal != null ? principal.getName() : null;
        CartResponse response = this.orderService.addToCart(cartId, buyerEmail, request);
        return ResponseEntity.ok().header("X-Cart-Id", response.getCartId()).body((Object)response);
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(value="X-Cart-Id", required=false) String cartId, Principal principal) {
        try {
            String buyerEmail = principal != null ? principal.getName() : null;
            CartResponse response = this.orderService.getCart(cartId, buyerEmail);
            return ResponseEntity.ok().header("X-Cart-Id", response.getCartId()).body((Object)response);
        }
        catch (OrderBaseException e) {
            return ResponseEntity.ok((Object)e.getMessage());
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateCartItem(@RequestHeader(value="X-Cart-Id") String cartId, @PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request, Principal principal) {
        try {
            String buyerEmail = principal != null ? principal.getName() : null;
            CartResponse response = orderService.updateCartItem(cartId, buyerEmail, itemId, request);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @DeleteMapping(value={"/items/{itemId}"})
    public ResponseEntity<?> removeFromCart(@RequestHeader(value="X-Cart-Id") String cartId, @PathVariable Long itemId, Principal principal) {
        try {
            String buyerEmail = principal != null ? principal.getName() : null;
            Long response = orderService.removeFromCart(cartId, buyerEmail, itemId);
            return ResponseEntity.ok((Object)response);
        }
        catch (OrderBaseException e) {
            return ResponseEntity.ok((Object)e.getMessage());
        }
    }

    @DeleteMapping(value={"/clear"})
    public ResponseEntity<?> clearCart(@RequestParam String email) {
        try {
            orderService.clearCart(email);
            return ResponseEntity.ok().build();
        }
        catch (OrderBaseException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping(value={"/checkout"})
    public ResponseEntity<?> checkout(@RequestHeader(value="X-Cart-Id") String cartId, @Valid @RequestBody CheckoutRequest request) {
        try {
            OrderResponse response = orderService.checkout(cartId, request);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}

