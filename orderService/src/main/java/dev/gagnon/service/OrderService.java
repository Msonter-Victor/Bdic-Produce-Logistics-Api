package dev.gagnon.service;

import dev.gagnon.dto.request.AddToCartRequest;
import dev.gagnon.dto.request.CartItemDto;
import dev.gagnon.dto.request.CheckoutRequest;
import dev.gagnon.dto.request.UpdateCartItemRequest;
import dev.gagnon.dto.response.*;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import dev.gagnon.model.*;
import dev.gagnon.repository.CartRepository;
import dev.gagnon.repository.OrderRepository;
import dev.gagnon.utils.EventPublisherAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private static final int MAX_CART_ITEMS = 20;
    private final EventPublisherAdapter eventPublisher;
    
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    public CartResponse addToCart(String cartId, String buyerEmail, AddToCartRequest request) {
        ProductResponseDto product = productServiceClient.getProduct(request.getProductId());
        validateProductAvailability(product, request.getQuantity());

        Cart cart = getOrCreateCart(cartId, buyerEmail);

        if (cart.getItems().size() >= MAX_CART_ITEMS) {
            throw new BusinessException("Cart cannot contain more than " + MAX_CART_ITEMS + " items");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(product.getId()))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setName(product.getName());
            newItem.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
            newItem.setProductImage(product.getMainImageUrl());
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    public CartResponse getCart(String cartId, String buyerEmail) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // Validate all items are still available
        List<CartItem> validItems = cart.getItems().stream()
            .filter(item -> {
                try {
                    ProductResponseDto product = productServiceClient.getProduct(item.getProductId());
                    return product != null;
                } catch (Exception e) {
                    return false;
                }
            })
            .collect(Collectors.toList());

        if (validItems.size() != cart.getItems().size()) {
            cart.setItems(validItems);
            cart = cartRepository.save(cart);
        }
        
        return mapToCartResponse(cart);
    }

    public CartResponse updateCartItem(String cartId, String buyerEmail, Long itemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        if (request.getQuantity() <= 0) {
            throw new BusinessException("Quantity must be positive");
        }

        ProductResponseDto product = productServiceClient.getProduct(item.getProductId());
        validateProductAvailability(product, request.getQuantity());

        item.setQuantity(request.getQuantity());
        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    public CartResponse removeFromCart(String cartId, String buyerEmail, Long itemId) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        
        if (!removed) {
            throw new ResourceNotFoundException("Item not found in cart");
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    public void clearCart(String cartId, String buyerEmail) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public OrderResponse checkout(String cartId, String buyerEmail, CheckoutRequest request) {
        if (buyerEmail == null) {
            throw new BusinessException("Guest checkout not supported. Please login to proceed.");
        }

        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot checkout empty cart");
        }

        // Validate all items and calculate total
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            ProductResponseDto product = productServiceClient.getProduct(item.getProductId());
            validateProductAvailability(product, item.getQuantity());
            subtotal = subtotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Process each item and update stock
        for (CartItem item : cart.getItems()) {
            ProductResponseDto product = productServiceClient.getProduct(item.getProductId());
            validateProductAvailability(product, item.getQuantity());

            // Reduce stock by purchased quantity (negative quantity change)
            productServiceClient.updateProductStock(item.getProductId(), -item.getQuantity());
        }

        // Create order
        Order order = new Order();
        order.setBuyerEmail(buyerEmail);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setMethod(request.getDeliveryMethod());
        deliveryInfo.setAddress(request.getAddress());
        order.setDeliveryInfo(deliveryInfo);
        
        order.setTotalAmount(subtotal);
        order.setDeliveryFee(calculateDeliveryFee(request.getDeliveryMethod()));
        order.setGrandTotal(order.getTotalAmount().add(order.getDeliveryFee()));

        // Convert cart items to order items
        List<OrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setProductName(cartItem.getName());
                orderItem.setUnitPrice(cartItem.getUnitPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setProductImage(cartItem.getProductImage());
                orderItem.setOrder(order);
                return orderItem;
            })
            .collect(Collectors.toList());

        order.setItems(orderItems);
        order.setCreatedAt(LocalDateTime.now());

        // Save order and clear cart
        Order savedOrder = orderRepository.save(order);
        eventPublisher.sendCreateMessage(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(String buyerEmail) {
        List<Order> orders = orderRepository.findByBuyerEmailOrderByCreatedAtDesc(buyerEmail);
        return orders.stream()
            .map(this::mapToOrderResponse)
            .collect(Collectors.toList());
    }

    public OrderResponse getOrder(String orderNumber, String buyerEmail) {
        Order order = orderRepository.findByOrderNumberAndBuyerEmail(orderNumber, buyerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToOrderResponse(order);
    }

    public OrderResponse updateOrderStatus(String orderNumber, OrderStatus status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    private Cart getOrCreateCart(String cartId, String buyerEmail) {
        Optional<Cart> cartOptional = buyerEmail != null 
            ? cartRepository.findByBuyerEmail(buyerEmail)
            : cartRepository.findByCartId(cartId);

        if (cartOptional.isPresent()) {
            return cartOptional.get();
        }

        Cart cart = new Cart();
        if (buyerEmail != null) {
            cart.setBuyerEmail(buyerEmail);
        } else {
            cart.setCartId(cartId != null ? cartId : UUID.randomUUID().toString());
        }
        cart.setCreatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    private void validateProductAvailability(ProductResponseDto product, int quantity) {
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        if (!productServiceClient.checkProductAvailability(product.getId(), quantity)) {
            throw new BusinessException("Product " + product.getName() + " is not available in requested quantity");
        }
    }

    private BigDecimal calculateDeliveryFee(String deliveryMethod) {
        return "delivery".equalsIgnoreCase(deliveryMethod) 
            ? BigDecimal.valueOf(2000) // NGN 2000 delivery fee
            : BigDecimal.ZERO;
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getCartId());
        response.setItems(cart.getItems().stream()
            .map(this::mapToCartItemDto)
            .collect(Collectors.toList()));
        
        BigDecimal totalPrice = cart.getItems().stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalItems = cart.getItems().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
        
        response.setTotalAmount(totalPrice);
        response.setTotalItems(totalItems);
        return response;
    }

    private CartItemDto mapToCartItemDto(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setProductId(item.getProductId());
        dto.setName(item.getName());
        dto.setPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setImageUrl(item.getProductImage());
        return dto;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus());
        response.setDeliveryInfo(order.getDeliveryInfo());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setTotalAmount(order.getTotalAmount());
        response.setGrandTotal(order.getGrandTotal());
        response.setCreatedAt(order.getCreatedAt());

        response.setItems(order.getItems().stream()
            .map(this::mapToOrderItemDto)
            .collect(Collectors.toList()));
        
        return response;
    }

    private OrderItemDto mapToOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProductId());
        dto.setName(item.getProductName());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setProductImage(item.getProductImage());
        return dto;
    }
}