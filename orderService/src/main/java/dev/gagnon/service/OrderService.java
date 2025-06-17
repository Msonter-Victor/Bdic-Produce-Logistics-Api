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
import dev.gagnon.repository.OrderItemRepository;
import dev.gagnon.repository.OrderRepository;
import dev.gagnon.utils.EventPublisherAdapter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final OrderItemRepository orderItemRepository;
    
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final ModelMapper modelMapper;

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
    public OrderResponse checkout(String cartId, CheckoutRequest request) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, request.getBuyerEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot checkout empty cart");
        }

        // Validate all items and calculate total
        BigDecimal subtotal = calculateSubtotal(cart);

        // Create order
        Order order = createOrder(request, subtotal);
        Order savedOrder = orderRepository.save(order); // Persist order first to generate ID

        // Create and save order items
        createOrderItems(cart, savedOrder);

        // Process inventory updates
        updateInventory(cart);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);
        OrderCreatedDto orderCreatedDto = modelMapper.map(savedOrder, OrderCreatedDto.class);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(savedOrder.getId());
        List<OrderItemDto> orderItemDtoList = orderItems.stream()
                        .map(OrderItemDto::new)
                                .collect(Collectors.toList());
        orderCreatedDto.setOrderItemDtoList(orderItemDtoList);
        eventPublisher.sendCreateMessage(orderCreatedDto);


        return mapToOrderResponse(savedOrder);
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> {
                    ProductResponseDto product = productServiceClient.getProduct(item.getProductId());
                    validateProductAvailability(product, item.getQuantity());
                    return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order createOrder(CheckoutRequest request, BigDecimal subtotal) {
        Order order = new Order();
        order.setBuyerEmail(request.getBuyerEmail());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setMethod(request.getDeliveryMethod());
        deliveryInfo.setAddress(request.getAddress());
        order.setDeliveryInfo(deliveryInfo);

        order.setTotalAmount(subtotal);
        order.setDeliveryFee(calculateDeliveryFee(request.getDeliveryMethod()));
        order.setGrandTotal(order.getTotalAmount().add(order.getDeliveryFee()));

        return order;
    }

    private void createOrderItems(Cart cart, Order order) {
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order); // Only set the parent reference
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setProductName(cartItem.getName());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setProductImage(cartItem.getProductImage());
                    return orderItem;
                })
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems); // Batch insert
    }

    private void updateInventory(Cart cart) {
        cart.getItems().forEach(item -> {
            productServiceClient.updateProductStock(
                    item.getProductId(),
                    -item.getQuantity()
            );
        });
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
        dto.setItemId(item.getId());
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

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDto> orderItemDtoList = orderItems.stream()
                .map(OrderItemDto::new)
                .toList();
        response.setItems(orderItemDtoList);
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