//package dev.gagnon.Service;//package dev.gagnon.service;
//
//import dev.gagnon.dto.request.AddToCartRequest;
//import dev.gagnon.dto.request.CheckoutRequest;
//import dev.gagnon.dto.request.UpdateCartItemRequest;
//import dev.gagnon.dto.response.*;
//import dev.gagnon.Model.*;
//import dev.gagnon.repository.CartItemRepository;
//import dev.gagnon.repository.CartRepository;
//import dev.gagnon.repository.OrderItemRepository;
//import dev.gagnon.repository.OrderRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.Random;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class OrderService {
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private OrderItemRepository orderItemRepository;
//
//    @Autowired
//    private ProductServiceClient productServiceClient;
//
//    @Autowired
//    private UserServiceClient userServiceClient;
//
//
//    // CART OPERATIONS
//    public CartResponse addToCart(AddToCartRequest request) {
//        // Validate product exists and is available
//        ProductResponse product = productServiceClient.getProduct(request.getProductId());
//        if (product == null) {
//            throw new RuntimeException("Product not found: " + request.getProductId());
//        }
//
////        if (productServiceClient.checkProductAvailability(request.getProductId(), request.getQuantity())) {
////            throw new RuntimeException("Insufficient product stock");
////        }
//
//        // Get or create cart
//        Cart cart = cartRepository.findByUserId(request.getUserId())
//                .orElse(new Cart(null, request.getUserId(), new ArrayList<>(), null, null));
//
//        if (cart.getId() == null) {
//            cart = cartRepository.save(cart);
//        }
//
//        // Check if item already exists in cart
//        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());
//
//        if (existingItem.isPresent()) {
//            // Update quantity
//            CartItem item = existingItem.get();
//            int newQuantity = item.getQuantity() + request.getQuantity();
//
//            if (productServiceClient.checkProductAvailability(request.getProductId(), newQuantity)) {
//                throw new RuntimeException("Insufficient product stock for updated quantity");
//            }
//
//            item.setQuantity(newQuantity);
//            cartItemRepository.save(item);
//        } else {
//            // Add new item
//            CartItem newItem = new CartItem();
//            newItem.setCart(cart);
//            newItem.setProductId(request.getProductId());
//            newItem.setQuantity(request.getQuantity());
////            newItem.setUnitPrice(product.getProductPrice());
////            newItem.setProductName(product.getProductName());
////            newItem.setProductImage(product.getImageUrl());
//
//            cartItemRepository.save(newItem);
//            cart.getItems().add(newItem);
//        }
//
//        return convertToCartResponse(cartRepository.save(cart));
//    }
//
//    public CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
//
//        CartItem item = cartItemRepository.findById(itemId)
//                .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));
//
//        if (!item.getCart().getId().equals(cart.getId())) {
//            throw new RuntimeException("Cart item does not belong to user");
//        }
//
//        // Check product availability
//        if (productServiceClient.checkProductAvailability(item.getProductId(), request.getQuantity())) {
//            throw new RuntimeException("Insufficient product stock");
//        }
//
//        item.setQuantity(request.getQuantity());
//        cartItemRepository.save(item);
//
//        return convertToCartResponse(cartRepository.findById(cart.getId()).orElse(cart));
//    }
//
//    public CartResponse removeFromCart(Long userId, Long itemId) {
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
//
//        CartItem item = cartItemRepository.findById(itemId)
//                .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));
//
//        if (!item.getCart().getId().equals(cart.getId())) {
//            throw new RuntimeException("Cart item does not belong to user");
//        }
//
//        cartItemRepository.delete(item);
//
//        return convertToCartResponse(cartRepository.findById(cart.getId()).orElse(cart));
//    }
//
//    public CartResponse getCart(Long userId) {
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElse(new Cart(null, userId, new ArrayList<>(), null, null));
//
//        return convertToCartResponse(cart);
//    }
//
//    public void clearCart(Long userId) {
//        cartRepository.deleteByUserId(userId);
//    }
//
//    // ORDER OPERATIONS
//    public OrderResponse checkout(Long userId, CheckoutRequest request) {
//        // Get user cart
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Cart is empty"));
//
//        if (cart.getItems().isEmpty()) {
//            throw new RuntimeException("Cart is empty");
//        }
//
//        // Validate all products are still available
//        for (CartItem item : cart.getItems()) {
//            if (productServiceClient.checkProductAvailability(item.getProductId(), item.getQuantity())) {
//                throw new RuntimeException("Product " + " is no longer available in requested quantity");
//            }
//        }
//
//        // Create order
//        Order order = new Order();
//        order.setOrderNumber(generateOrderNumber());
//        order.setUserId(userId);
////        order.setTotalAmount(cart.getTotalAmount());
//
//        // Set delivery info
//        DeliveryInfo deliveryInfo = new DeliveryInfo();
//        deliveryInfo.setMethod(request.getDeliveryMethod());
//
//        if (request.getDeliveryMethod() == DeliveryMethod.HOME_DELIVERY) {
//            deliveryInfo.setAddress(request.getAddress());
//        } else {
//            deliveryInfo.setShopLocation(request.getShopLocation());
//            deliveryInfo.setPickupTime(request.getPickupTime());
//        }
//
//        order.setDeliveryInfo(deliveryInfo);
//        order.setGrandTotal(order.getTotalAmount().add(order.getDeliveryFee()));
//
//        // Save order
//        order = orderRepository.save(order);
//
//        // Create order items
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (CartItem cartItem : cart.getItems()) {
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProductId(cartItem.getProductId());
//            orderItem.setQuantity(cartItem.getQuantity());
////            orderItem.setUnitPrice(cartItem.getUnitPrice());
////            orderItem.setProductName(cartItem.getProductName());
////            orderItem.setProductImage(cartItem.getProductImage());
////
//            orderItems.add(orderItem);
//        }
//
//        orderItemRepository.saveAll(orderItems);
//        order.setItems(orderItems);
//
//        // Update product stock
//        for (CartItem item : cart.getItems()) {
//            productServiceClient.updateProductStock(item.getProductId(), -item.getQuantity());
//        }
//
//        // Clear cart
//        clearCart(userId);
//
//        // Update order status
//        order.setStatus(OrderStatus.CONFIRMED);
//        orderRepository.save(order);
//
//        return convertToOrderResponse(order);
//    }
//
//    public List<OrderResponse> getUserOrders(Long userId) {
//        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
//        return orders.stream()
//                .map(this::convertToOrderResponse)
//                .collect(Collectors.toList());
//    }
//
//    public OrderResponse getOrder(Long userId, String orderNumber) {
//        Order order = orderRepository.findByOrderNumber(orderNumber)
//                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
//
//        if (!order.getUserId().equals(userId)) {
//            throw new RuntimeException("Order does not belong to user");
//        }
//
//        return convertToOrderResponse(order);
//    }
//
//    public OrderResponse updateOrderStatus(String orderNumber, OrderStatus status) {
//        Order order = orderRepository.findByOrderNumber(orderNumber)
//                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
//
//        order.setStatus(status);
//        orderRepository.save(order);
//
//        return convertToOrderResponse(order);
//    }
//
//    // HELPER METHODS
//    private String generateOrderNumber() {
//        return "ORD" + System.currentTimeMillis() + String.format("%03d", new Random().nextInt(1000));
//    }
//
//    private BigDecimal calculateDeliveryFee(String city) {
//        // Implement your delivery fee calculation logic
//        // This is a simple example
//        if ("Lagos".equalsIgnoreCase(city)) {
//            return new BigDecimal("500.00");
//        } else {
//            return new BigDecimal("1000.00");
//        }
//    }
//
//    private CartResponse convertToCartResponse(Cart cart) {
//        List<CartItemResponse> itemResponses = cart.getItems().stream()
//                .map(this::convertToCartItemResponse)
//                .collect(Collectors.toList());
//
//        return CartResponse.builder()
//                .id(cart.getId())
//                .userId(cart.getUserId())
//                .items(itemResponses)
////                .totalAmount(cart.getTotalAmount())
//                .totalItems(cart.getItems().stream().mapToInt(CartItem::getQuantity).sum())
//                .createdAt(cart.getCreatedAt())
//                .updatedAt(cart.getUpdatedAt())
//                .build();
//    }
//
//    private CartItemResponse convertToCartItemResponse(CartItem item) {
//        return CartItemResponse.builder()
//                .id(item.getId())
//                .productId(item.getProductId())
////                .productName(item.getProductName())
////                .productImage(item.getProductImage())
//                .quantity(item.getQuantity())
////                .unitPrice(item.getUnitPrice())
////                .totalPrice(item.getTotalPrice())
//                .build();
//    }
//
//    private OrderResponse convertToOrderResponse(Order order) {
//        List<OrderItemResponse> itemResponses = order.getItems().stream()
//                .map(this::convertToOrderItemResponse)
//                .collect(Collectors.toList());
//
//        return OrderResponse.builder()
//                .id(order.getId())
//                .orderNumber(order.getOrderNumber())
//                .userId(order.getUserId())
//                .items(itemResponses)
//                .deliveryInfo(order.getDeliveryInfo())
//                .totalAmount(order.getTotalAmount())
//                .deliveryFee(order.getDeliveryFee())
//                .grandTotal(order.getGrandTotal())
//                .status(order.getStatus())
//                .createdAt(order.getCreatedAt())
//                .build();
//    }
//
//    private OrderItemResponse convertToOrderItemResponse(OrderItem item) {
//        return OrderItemResponse.builder()
//                .id(item.getId())
//                .productId(item.getProductId())
////                .productName(item.getProductName())
////                .productImage(item.getProductImage())
//                .quantity(item.getQuantity())
////                .unitPrice(item.getUnitPrice())
////                .totalPrice(item.getTotalPrice())
//                .build();
//    }
//}