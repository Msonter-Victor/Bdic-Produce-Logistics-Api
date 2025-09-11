
package dev.gagnon.controller;

import dev.gagnon.dto.request.AddToWishlistRequest;
import dev.gagnon.dto.request.CheckoutRequest;
import dev.gagnon.dto.request.ProcessItemRequest;
import dev.gagnon.dto.request.ProcessRequest;
import dev.gagnon.dto.response.*;
import dev.gagnon.exception.OrderBaseException;
import dev.gagnon.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value={"/api/orders"})
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping(value={"/user"})
    public ResponseEntity<List<BuyerOrderResponse>> getUserOrders(@RequestParam String buyerEmail) {
        List<BuyerOrderResponse> orders = orderService.getUserOrders(buyerEmail);
        return ResponseEntity.ok(orders);
    }

    @PostMapping(value={"/buy-product"})
    public ResponseEntity<?> buyProduct(@RequestParam Long productId, @RequestBody CheckoutRequest request) {
        try {
            String response = this.orderService.buyProduct(productId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value={"/all"})
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> allOrders = this.orderService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    @PostMapping(value={"/add-to-wishlist"})
    public ResponseEntity<?> addToWishlist(@RequestBody AddToWishlistRequest request) {
        AddToWishlistResponse response = this.orderService.addToWishList(request);
        return ResponseEntity.ok((Object)response);
    }

    @PostMapping(value={"/remove-from-wishlist"})
    public ResponseEntity<?> removeFromWishList(@RequestBody AddToWishlistRequest request) {
        AddToWishlistResponse response = this.orderService.removeFromWishList(request);
        return ResponseEntity.ok((Object)response);
    }

    @GetMapping(value={"/get-wishlist"})
    public ResponseEntity<?> getUserWishlist(@RequestParam String buyerEmail) {
        WishListResponse wishList = this.orderService.getUserWishList(buyerEmail);
        return ResponseEntity.ok((Object)wishList);
    }

    @GetMapping(value={"/get-shop-orders"})
    public ResponseEntity<?> getShopOrders(@RequestParam Long shopId) {
        List<OrderResponse> orders = this.orderService.getShopOrders(shopId);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping(value={"/all"})
    public ResponseEntity<?> deleteAllOrders() {
        String response = this.orderService.deleteAllOrders();
        return ResponseEntity.ok((Object)response);
    }

    @GetMapping(value={"/getShopTransactionAmount"})
    public ResponseEntity<?> getShopTransactionAmount(@RequestParam Long shopId) {
        Double totalAmount = this.orderService.getShopTransactionAmount(shopId);
        return ResponseEntity.ok((Object)totalAmount);
    }

    @GetMapping(value={"/getShopTransactionCount"})
    public ResponseEntity<?> getShopTransactionCount(@RequestParam Long shopId) {
        Long totalAmount = this.orderService.getShopTransactionCount(shopId);
        return ResponseEntity.ok((Object)totalAmount);
    }

    @GetMapping(value={"/getMarketTransactionAmount"})
    public ResponseEntity<?> getMarketTransactionAmount(@RequestParam Long marketId) {
        Double totalAmount = this.orderService.getMarketTransactionAmount(marketId);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping(value={"/getMarketTransactionCount"})
    public ResponseEntity<?> getMarketTransactionCount(@RequestParam Long marketId) {
        Long totalCount = this.orderService.getMarketTransactionCount(marketId);
        return ResponseEntity.ok((Object)totalCount);
    }

    @GetMapping(value={"/getAllOrderTransactions"})
    public ResponseEntity<?> getAllOrderTransactions() {
        List<OrderResponse> orders = this.orderService.getAllOrderTransactions();
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value={"/getTotalTransactionAmount"})
    public ResponseEntity<?> getTotalTransactionAmount() {
        Double totalAmount = this.orderService.getTotalTransactionAmount();
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping(value={"/getPayoutAmount"})
    public ResponseEntity<?> getUserTotalPayoutAmount(@RequestParam Long shopId) {
        Double totalAmount = this.orderService.getShopPayoutAmount(shopId);
        return ResponseEntity.ok(totalAmount);
    }

    @PostMapping(value={"/process-order"})
    public ResponseEntity<?> processOrder(@RequestParam Long id) {
        try {
            String response = this.orderService.processOrder(id);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value={"/ship-order"})
    public ResponseEntity<?> shipOrder(@RequestParam Long id) {
        try {
            String response = orderService.shipOrder(id);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(value={"/decline-order"})
    public ResponseEntity<?> declineOrder(@RequestParam Long id) {
        try {
            String response = this.orderService.declineOrder(id);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value={"/getShopStatistics"})
    public ResponseEntity<?> getShopStatistics(@RequestParam Long shopId) {
        try {
            ShopStatistic response = this.orderService.getShopOrderStatistics(shopId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value={"/process-item"})
    public ResponseEntity<?> processItem(@RequestBody ProcessItemRequest request) {
        try {
            String response = this.orderService.processItem(request);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value={"/ship-item"})
    public ResponseEntity<?> shipItem(@RequestBody ProcessItemRequest request) {
        try {
            String response = this.orderService.shipItem(request);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value={"/validate-pickUp"})
    public ResponseEntity<?> validateOrder(@RequestBody ProcessRequest request) {
        String orderNumber = request.getOrderNumber();
        String response = this.orderService.validateOrder(orderNumber);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value={"/user/soft-delete"})
    public ResponseEntity<?> softDeleteUserOrders(@RequestParam String buyerEmail) {
        String response = this.orderService.softDeleteUserOrders(buyerEmail);
        return ResponseEntity.ok(response);
    }
}

