package dev.gagnon.service;

import dev.gagnon.dto.request.*;
import dev.gagnon.dto.response.*;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import dev.gagnon.model.*;
import dev.gagnon.repository.*;
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
@RequiredArgsConstructor
public class OrderService {
    private static final int MAX_CART_ITEMS = 20;
    private final EventPublisherAdapter eventPublisher;
    private final OrderItemRepository orderItemRepository;
    private final WishListRepository wishListRepository;

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final WishListItemRepository wishListItemRepository;
    private final ModelMapper modelMapper;

    @Transactional
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

    @Transactional
    public CartResponse getCart(String cartId, String buyerEmail) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
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

    @Transactional
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

    @Transactional
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

    @Transactional
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
        BigDecimal subtotal = calculateSubtotal(cart);
        Order order = createOrder(request, subtotal);
        order = orderRepository.save(order);
        createOrderItems(cart, order);
        order = orderRepository.save(order);
        updateInventory(cart);
        cart.getItems().clear();
        cartRepository.save(cart);

        // Publish event
        OrderCreatedDto orderCreatedDto = modelMapper.map(order, OrderCreatedDto.class);
        List<OrderItemDto> orderItemDtoList = order.getItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
        orderCreatedDto.setOrderItemDtoList(orderItemDtoList);
        eventPublisher.sendCreateMessage(orderCreatedDto);

        return mapToOrderResponse(order);
    }

    private void createOrderItems(Cart cart, Order order) {
        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getName());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductImage(cartItem.getProductImage());
            order.addOrderItem(orderItem);
        });
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

    private void updateInventory(Cart cart) {
        cart.getItems().forEach(item -> {
            productServiceClient.updateProductStock(
                    item.getProductId(),
                    -item.getQuantity()
            );
        });
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String buyerEmail) {
        List<Order> orders = orderRepository.findByBuyerEmail(buyerEmail);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String orderNumber, String buyerEmail) {
        Order order = orderRepository.findByOrderNumberAndBuyerEmail(orderNumber, buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToOrderResponse(order);
    }

    @Transactional
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
        List<OrderItemDto> orderItemDtoList = order.getItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
        response.setItems(orderItemDtoList);
        return response;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    public AddToWishlistResponse addToWishList(AddToWishlistRequest request) {
        Optional<WishList> optionalWishList = wishListRepository.findByBuyerEmail(request.getBuyerEmail());

        WishList wishList = optionalWishList.orElseGet(() -> {
            WishList newWishList = new WishList();
            newWishList.setBuyerEmail(request.getBuyerEmail());
            return wishListRepository.save(newWishList);
        });
        ProductResponseDto product = productServiceClient.getProduct(request.getProductId());
        WishListItem item = modelMapper.map(product, WishListItem.class);
        item.setWishlist(wishList);
        wishListItemRepository.save(item);
        return modelMapper.map(wishList, AddToWishlistResponse.class);
    }

    public WishListResponse getUserWishList(String buyerEmail) {
        WishList wishList = wishListRepository.findByBuyerEmail(buyerEmail).get();
        List<WishListItem> items = wishListItemRepository.findByWishlistId(wishList.getId());
        WishListResponse response = new WishListResponse();
        response.setId(wishList.getId());
        response.setBuyerEmail(wishList.getBuyerEmail());
        response.setWishList(items);
        return response;
    }

}