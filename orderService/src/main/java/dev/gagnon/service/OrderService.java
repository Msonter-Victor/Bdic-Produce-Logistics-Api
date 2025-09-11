package dev.gagnon.service;

import dev.gagnon.dto.request.*;
import dev.gagnon.dto.response.*;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import dev.gagnon.model.*;
import dev.gagnon.model.constants.ItemStatus;
import dev.gagnon.model.constants.OrderStatus;
import dev.gagnon.repository.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderItemRepository orderItemRepository;
    private final WishListRepository wishListRepository;
    private final ShopRepository shopRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ServiceClient serviceClient;
    private final WishListItemRepository wishListItemRepository;
    private final ModelMapper modelMapper;
    private final CartItemRepository cartItemRepository;
    private final MarketRepository marketRepository;

    @Transactional(readOnly=true)
    public List<BuyerOrderResponse> getUserOrders(String buyerEmail) {
        List<Order> parentOrders = this.orderRepository.findByBuyerEmailAndIsParentOrderTrue(buyerEmail);
        return parentOrders.stream().map(parentOrder -> {
            List<Order> childOrders = this.orderRepository.findByParentOrderId(parentOrder.getId());
            log.info("🔍 Found {} child orders for parent order {}", childOrders.size(), parentOrder.getOrderNumber());
            BuyerOrderResponse response = new BuyerOrderResponse();
            response.setId(parentOrder.getParentOrderId());
            response.setOrderNumber(parentOrder.getOrderNumber());
            response.setStatus(String.valueOf(parentOrder.getStatus()));
            response.setDeliveryInfo(parentOrder.getDeliveryInfo());
            response.setCreatedAt(parentOrder.getCreatedAt());
            response.setTotalAmount(parentOrder.getTotalAmount());
            response.setDeliveryFee(parentOrder.getDeliveryFee());
            response.setGrandTotal(parentOrder.getGrandTotal());
            // Create flat list of all items from child orders
            List<OrderItemDto> allItems = childOrders.stream()
                    .flatMap(order -> {
                        log.info("🔍 Flattening {} items from order {}", order.getItems().size(), order.getId());
                        return order.getItems().stream();
                    })
                    .map(OrderItemDto::new)
                    .collect(Collectors.toList());
            response.setItems(allItems);
            log.info("🔍 Total flattened items for order {}: {}", parentOrder.getOrderNumber(), allItems.size());
            log.info("🔍 Final response for order {}: {} items",
                    response.getOrderNumber(),
                    response.getItems() != null ? response.getItems().size() : 0
            );
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    public CartResponse addToCart(String cartId, String buyerEmail, AddToCartRequest request) {
        ProductResponse product = this.serviceClient.getProduct(request.getProductId());
        this.validateProductAvailability(product, request.getQuantity());
        Cart cart = this.getOrCreateCart(cartId, buyerEmail);
        if (cart.getItems().size() >= 15) {
            throw new BusinessException("Cart cannot contain more than 15 items");
        }
        Optional<CartItem> existingItem = cart.getItems().stream().filter(item -> item.getProductId().equals(product.getId())).findFirst();
        if (existingItem.isPresent()) {
            CartItem item2 = existingItem.get();
            item2.setQuantity(item2.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setShopId(product.getShopId());
            newItem.setVendorEmail(product.getVendorEmail());
            newItem.setName(product.getName());
            newItem.setUnitPrice(product.getPrice());
            newItem.setProductImage(product.getMainImageUrl());
            newItem.setQuantity(request.getQuantity());
            this.cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        return this.mapToCartResponse(savedCart);
    }

    @Transactional
    public CartResponse getCart(String cartId, String buyerEmail) {
        Cart cart = this.cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        List validItems = cart.getItems().stream().filter(item -> {
            try {
                ProductResponse product = this.serviceClient.getProduct(item.getProductId());
                return product != null;
            }
            catch (Exception e) {
                return false;
            }
        }).collect(Collectors.toList());
        if (validItems.size() != cart.getItems().size()) {
            cart.setItems(validItems);
            cart = cartRepository.save(cart);
        }
        return this.mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(String cartId, String buyerEmail, Long itemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
        if (request.getQuantity() <= 0) {
            throw new BusinessException("Quantity must be positive");
        }
        ProductResponse product = this.serviceClient.getProduct(item.getProductId());
        this.validateProductAvailability(product, request.getQuantity());
        item.setQuantity(request.getQuantity());
        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        return this.mapToCartResponse(savedCart);
    }

    @Transactional
    public Long removeFromCart(String cartId, String buyerEmail, Long itemId) {
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, buyerEmail).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new ResourceNotFoundException("Item not found in cart");
        }
        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        if (savedCart.getItems().isEmpty()) {
            this.cartRepository.delete(cart);
            return 0L;
        }
        return (long) savedCart.getItems().size();
    }

    @Transactional
    public void clearCart(String buyerEmail) {
        Cart cart = this.cartRepository.findByBuyerEmail(buyerEmail).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        List<Long> cartItemsIds = cart.getItems().stream().map(CartItem::getId).toList();
        cart.getItems().clear();
        this.cartItemRepository.deleteAllById(cartItemsIds);
        cart.setUpdatedAt(LocalDateTime.now());
        this.cartRepository.delete(cart);
    }

    @Transactional
    public OrderResponse checkout(String cartId, CheckoutRequest request) {
        if (this.orderRepository.existsByTransactionReference(request.getReference())) {
            throw new BusinessException("Order already created");
        }
        Cart cart = cartRepository.findByCartIdOrBuyerEmail(cartId, request.getBuyerEmail()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot checkout empty cart");
        }

        boolean hasSelfPurchase = cart.getItems().stream().anyMatch(item -> item.getVendorEmail().equalsIgnoreCase(request.getBuyerEmail()));
        if (hasSelfPurchase) {
            throw new BusinessException("You cannot purchase your own products");
        }
        Map<Long, List<CartItem>> itemsByShop = cart.getItems().stream().collect(Collectors.groupingBy(CartItem::getShopId));
        Order parentOrder = this.createParentOrder(request, cart.getItems());
        parentOrder = orderRepository.save(parentOrder);
        NotificationEvent event = new NotificationEvent();
        event.setEmail(parentOrder.getBuyerEmail());
        event.setTitle("Order has been created with id: " + parentOrder.getOrderNumber());
        event.setMessage("Your order has been created successfully, visit your orders to view");
        event.setType("INFO");
        serviceClient.sendNotification(event);
        ArrayList<Order> shopOrders = new ArrayList<>();
        BigDecimal totalParentAmount = BigDecimal.ZERO;
        BigDecimal totalParentDeliveryFee = BigDecimal.valueOf(request.getDeliveryFee());
        for (Map.Entry<Long, List<CartItem>> entry : itemsByShop.entrySet()) {
            Long shopId = entry.getKey();
            List<CartItem> shopItems = entry.getValue();
            Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
            BigDecimal subtotal = this.calculateSubtotal(shopItems);
            BigDecimal deliveryFee = BigDecimal.valueOf(request.getDeliveryFee());
            Order shopOrder = this.createShopOrder(request, subtotal, shop, parentOrder);
            shopOrder = orderRepository.save(shopOrder);
            event.setEmail(shopOrder.getShop().getUser().getEmail());
            event.setType("INFO");
            serviceClient.sendNotification(event);
            createOrderItems(shopItems, shopOrder);
            shopOrders.add(shopOrder);
            totalParentAmount = totalParentAmount.add(subtotal);
            totalParentDeliveryFee = totalParentDeliveryFee.add(deliveryFee);
        }
        parentOrder.setTotalAmount(totalParentAmount);
        parentOrder.setDeliveryFee(totalParentDeliveryFee);
        parentOrder.setGrandTotal(totalParentAmount.add(totalParentDeliveryFee));
        parentOrder = orderRepository.save(parentOrder);
        if (request.getDeliveryMethod().equalsIgnoreCase("delivery"))
            serviceClient.createOrder(parentOrder.getId());
        OrderResponse response = this.mapToOrderResponse(parentOrder);
        response.setShopOrdersCount(shopOrders.size());
        return response;
    }

    private Order createShopOrder(CheckoutRequest request, BigDecimal subtotal, Shop shop, Order parentOrder) {
        Order order = new Order();
        order.setOrderNumber(parentOrder.getOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setShop(shop);
        order.setTransactionReference(request.getReference());
        order.setParentOrder(false);
        order.setBuyerEmail(request.getBuyerEmail());
        order.setParentOrderId(parentOrder.getId());
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setMethod(request.getDeliveryMethod());
        deliveryInfo.setAddress(request.getAddress());
        deliveryInfo.setContactPhone(request.getPhoneNumber());
        order.setDeliveryInfo(deliveryInfo);
        order.setTotalAmount(subtotal);
        order.setDeliveryFee(BigDecimal.valueOf(request.getDeliveryFee()));
        order.setGrandTotal(order.getTotalAmount().add(order.getDeliveryFee()));
        return order;
    }

    private Order createParentOrder(CheckoutRequest request, List<CartItem> allItems) {
        Order order = new Order();
        order.setBuyerEmail(request.getBuyerEmail());
        order.setOrderNumber(this.generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setParentOrder(true);
        order.setTransactionReference(request.getReference());
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setMethod(request.getDeliveryMethod());
        deliveryInfo.setAddress(request.getAddress());
        deliveryInfo.setContactPhone(request.getPhoneNumber());
        order.setDeliveryInfo(deliveryInfo);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setDeliveryFee(BigDecimal.valueOf(request.getDeliveryFee()));
        order.setGrandTotal(BigDecimal.ZERO);
        return order;
    }

    private BigDecimal calculateSubtotal(List<CartItem> items) {
        return items.stream().map(item -> {
            ProductResponse product = this.serviceClient.getProduct(item.getProductId());
            this.validateProductAvailability(product, item.getQuantity());
            return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void createOrderItems(List<CartItem> cartItems, Order order) {
        cartItems.forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getName());
            orderItem.setDescription(cartItem.getDescription());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setStatus(ItemStatus.PENDING);
            orderItem.setProductImage(cartItem.getProductImage());
            orderItem.setOrder(order);
            this.serviceClient.updateProductStock(orderItem.getProductId(), orderItem.getQuantity());
            this.orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);
        });
        this.orderRepository.save(order);
    }

    @Transactional(readOnly=true)
    public List<OrderResponse> getShopOrders(Long shopId) {
        shopRepository.findById(shopId).orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
        List<Order> shopOrders = this.orderRepository.findByShopIdAndIsParentOrderFalse(shopId);
        if (shopOrders.isEmpty()) {
            return List.of();
        }
        return shopOrders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }

    private Cart getOrCreateCart(String cartId, String buyerEmail) {
        Optional cartOptional;
        Optional optional = cartOptional = buyerEmail != null ? this.cartRepository.findByBuyerEmail(buyerEmail) : cartRepository.findByCartId(cartId);
        if (cartOptional.isPresent()) {
            return (Cart)cartOptional.get();
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

    private void validateProductAvailability(ProductResponse product, int quantity) {
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        if (!this.serviceClient.checkProductAvailability(product.getId(), Integer.valueOf(quantity))) {
            throw new BusinessException("Product " + product.getName() + " is not available in requested quantity");
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getCartId());
        response.setItems(cart.getItems().stream().map(this::mapToCartItemDto).collect(Collectors.toList()));
        BigDecimal totalPrice = cart.getItems().stream().map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity().intValue()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalItems = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
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
        response.setBuyerEmail(order.getBuyerEmail());
        response.setParentOrder(order.isParentOrder());
        if (order.getShop() != null) {
            response.setShopId(order.getShop().getId());
        }
        List<OrderItemDto> orderItemDtoList = order.getItems().stream().map(OrderItemDto::new).collect(Collectors.toList());
        response.setItems(orderItemDtoList);
        return response;
    }

    @Transactional(readOnly=true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }

    public AddToWishlistResponse addToWishList(AddToWishlistRequest request) {
        Optional<WishList> optionalWishList = this.wishListRepository.findByBuyerEmail(request.getBuyerEmail());
        WishList wishList = optionalWishList.orElseGet(() -> {
            WishList newWishList = new WishList();
            newWishList.setBuyerEmail(request.getBuyerEmail());
            return wishListRepository.save(newWishList);
        });
        List<WishListItem> items = this.wishListItemRepository.findByWishlistId(wishList.getId());
        for (int count = 0; count < items.toArray().length; ++count) {
            if (!(items.get(count)).getProductId().equals(request.getProductId())) continue;
            throw new BusinessException("Product already exists in wishlist");
        }
        ProductResponse product = this.serviceClient.getProduct(request.getProductId());
        WishListItem item = modelMapper.map(product, WishListItem.class);
        item.setUnitPrice(product.getPrice());
        item.setWishlist(wishList);
        this.wishListItemRepository.save(item);
        return new AddToWishlistResponse("Product added to wishlist successfully");
    }

    public WishListResponse getUserWishList(String buyerEmail) {
        Optional<WishList> wishListOptional = this.wishListRepository.findByBuyerEmail(buyerEmail);
        if (wishListOptional.isEmpty()) {
            WishListResponse response = new WishListResponse();
            response.setBuyerEmail(buyerEmail);
            response.setWishList(Collections.emptyList());
            return response;
        }
        WishList wishList = wishListOptional.get();
        List<WishListItem> items = this.wishListItemRepository.findByWishlistId(wishList.getId());
        WishListResponse response = new WishListResponse();
        response.setId(wishList.getId());
        response.setBuyerEmail(wishList.getBuyerEmail());
        response.setWishList(items);
        return response;
    }

    public AddToWishlistResponse removeFromWishList(AddToWishlistRequest request) {
        WishListItem item = wishListItemRepository.findByProductIdAndUser(request.getProductId(),request.getBuyerEmail()).orElseThrow(() -> new ResourceNotFoundException("Product not found in wishlist"));
        this.wishListItemRepository.delete(item);
        return new AddToWishlistResponse("Product removed from wishlist successfully");
    }

    public String deleteAllOrders() {
        this.orderRepository.deleteAll();
        return "All orders deleted successfully";
    }

    public Double getShopTransactionAmount(Long shopId) {
        List<OrderResponse> shopOrders = getShopOrders(shopId);
        return shopOrders.stream().mapToDouble(orderResponse -> orderResponse.getTotalAmount().doubleValue()).sum();
    }

    public Long getShopTransactionCount(Long shopId) {
        return (long) this.getShopOrders(shopId).size();
    }

    public Double getTotalTransactionAmount() {
        List<Order> parentOrders = this.orderRepository.findByParentOrder(true);
        return parentOrders.stream().mapToDouble(order -> order.getTotalAmount().doubleValue()).sum();
    }

    public List<OrderResponse> getAllOrderTransactions() {
        List<Order> parentOrders = this.orderRepository.findByParentOrder(true);
        return parentOrders.stream().map(this::mapToOrderResponse).toList();
    }

    public String processOrder(Long id) {
        Order order  = orderRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("order not found"));
        List<OrderItem> items = order.getItems();
        for (OrderItem orderItem : items) {
            orderItem.setStatus(ItemStatus.PROCESSING);
            orderItemRepository.save(orderItem);
        }
        order.setItems(items);
        order.setStatus(OrderStatus.PROCESSING);
        Order parentOrder = orderRepository
                .findById(order.getParentOrderId())
                .orElseThrow(()->new ResourceNotFoundException("parent order not found"));
        parentOrder.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
        orderRepository.save(parentOrder);
        return "order declined";
    }

    public String validateOrder(String orderNumber) {
        List<Order> orders = this.orderRepository.findAllByOrderNumber(orderNumber);
        orders.forEach(order -> order.setStatus(OrderStatus.DELIVERED));
        this.orderRepository.saveAll(orders);
        NotificationEvent event = new NotificationEvent();
        event.setEmail(orders.get(0).getShop().getUser().getEmail());
        event.setType("INFO");
        event.setTitle("Order confirmed");
        event.setMessage("Your order has been confirmed and marked delivered by customer");
        this.serviceClient.sendNotification(event);
        return "order updated successfully";
    }

    @Transactional
    public String softDeleteUserOrders(String buyerEmail) {
        List<Order> orders = this.orderRepository.findByBuyerEmailAndIsParentOrderTrue(buyerEmail);
        orders.forEach(order -> {
            order.setDeleted(true);
            order.setDeletedAt(LocalDateTime.now());
            order.setDeletedBy("system");
            this.orderRepository.save(order);
            List<Order> childOrders = this.orderRepository.findByParentOrderId(order.getId());
            childOrders.forEach(child -> {
                child.setDeleted(true);
                child.setDeletedAt(LocalDateTime.now());
                child.setDeletedBy("system");
                this.orderRepository.save(child);
            });
        });
        return "Successfully soft-deleted all orders for user: " + buyerEmail;
    }

    @Transactional
    public String buyProduct(Long productId, CheckoutRequest request) {
        if (this.orderRepository.existsByTransactionReference(request.getReference())) {
            throw new BusinessException("Order already created");
        }
        ProductResponse product = this.serviceClient.getProduct(productId);
        this.validateProductAvailability(product, 1);
        if (product.getVendorEmail().equalsIgnoreCase(request.getBuyerEmail())) {
            throw new BusinessException("You cannot purchase your own products");
        }
        Order parentOrder = this.createParentOrder(request, List.of(this.createCartItemFromProduct(product)));
        parentOrder = orderRepository.save(parentOrder);
        NotificationEvent event = new NotificationEvent();
        event.setEmail(parentOrder.getBuyerEmail());
        event.setTitle("Order has been created with id: " + parentOrder.getOrderNumber());
        event.setMessage("Your order has been created successfully, visit your orders to view");
        event.setType("INFO");
        this.serviceClient.sendNotification(event);
        Shop shop = shopRepository.findById(product.getShopId()).orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        List<CartItem> shopItems = List.of(this.createCartItemFromProduct(product));
        Order shopOrder = this.createShopOrder(request, product.getPrice(), shop, parentOrder);
        shopOrder = orderRepository.save(shopOrder);
        this.createOrderItems(shopItems, shopOrder);
        parentOrder.setTotalAmount(product.getPrice());
        parentOrder.setDeliveryFee(BigDecimal.valueOf(request.getDeliveryFee()));
        parentOrder.setGrandTotal(parentOrder.getTotalAmount().add(parentOrder.getDeliveryFee()));
        orderRepository.save(parentOrder);
        event.setEmail(product.getVendorEmail());
        event.setTitle("New order received - " + parentOrder.getOrderNumber());
        event.setMessage("You have received a new order for your product: " + product.getName());
        this.serviceClient.sendNotification(event);
        if (request.getDeliveryMethod().equalsIgnoreCase("delivery"))
            serviceClient.createOrder(parentOrder.getId());
        return "Order created successfully with number: " + parentOrder.getOrderNumber();
    }

    private CartItem createCartItemFromProduct(ProductResponse product) {
        CartItem item = new CartItem();
        item.setProductId(product.getId());
        item.setShopId(product.getShopId());
        item.setVendorEmail(product.getVendorEmail());
        item.setName(product.getName());
        item.setUnitPrice(product.getPrice());
        item.setProductImage(product.getMainImageUrl());
        item.setQuantity(1);
        return item;
    }

    public String shipOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.SHIPPED);
        Order parentOrder = orderRepository.findById(order.getParentOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent order not found"));
        parentOrder.setStatus(OrderStatus.PROCESSING);
        List<Order> orders = this.orderRepository.findAllByOrderNumber(order.getOrderNumber());
        AtomicInteger count = new AtomicInteger();
        orders.forEach(order1 -> {
            if (order1.getStatus()==OrderStatus.SHIPPED) count.getAndIncrement();
        });
        if (orders.size()==count.get())parentOrder.setStatus(OrderStatus.SHIPPED);
        NotificationEvent event = new NotificationEvent();
        event.setEmail(orders.get(0).getBuyerEmail());
        event.setType("INFO");
        event.setTitle("Order shipped");
        event.setMessage("Your order has been shipped by vendor");
        this.serviceClient.sendNotification(event);
        return "order updated successfully";
    }

    public String processItem(ProcessItemRequest request) {
        OrderAndItems orderAndItem = this.findOrderAndItem(request);
        List<OrderItem> items = orderAndItem.items();
        items.forEach(item -> this.serviceClient.updateProductStock(item.getProductId(), item.getQuantity()));
        updateOrderStatus(orderAndItem, OrderStatus.PROCESSING, ItemStatus.PROCESSING);
        return "item processed";
    }

    public String shipItem(ProcessItemRequest request) {
        OrderAndItems orderAndItem = this.findOrderAndItem(request);
        List<OrderItem> items = orderAndItem.items();
        Order order = orderAndItem.order();
        Shop shop = items.get(0).getShop();
        if (shop == null) {
            throw new IllegalStateException("Shop not found for items");
        }
        log.info("Shop: {}", shop);
        shop.setTotalPayoutAmount(BigDecimal.valueOf(shop.getTotalPayoutAmount().doubleValue() + order.getTotalAmount().doubleValue()));
        shopRepository.save(shop);
        updateOrderStatus(orderAndItem, OrderStatus.SHIPPED, ItemStatus.SHIPPED);
        return "item shipped";
    }

    public String declineOrder(Long id) {
        Order order  = orderRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("order not found"));
        List<OrderItem> items = order.getItems();
        for (OrderItem orderItem : items) {
            orderItem.setStatus(ItemStatus.DECLINED);
            orderItemRepository.save(orderItem);
        }
        order.setItems(items);
        order.setStatus(OrderStatus.DECLINED);
        orderRepository.save(order);
        return "order declined";
    }

    private String updateOrderStatus(OrderAndItems orderAndItem, OrderStatus orderStatus, ItemStatus itemStatus) {
        Order order = orderAndItem.order();
        List<OrderItem> items = orderAndItem.items();
        order.setStatus(orderStatus);
        for (OrderItem orderItem : items) {
            orderItem.setStatus(itemStatus);
        }

        orderRepository.save(order);
        orderItemRepository.saveAll(items);
        if (!order.isParentOrder() && order.getParentOrderId() != null) {
            this.updateParentOrderStatus(order.getParentOrderId(), orderStatus);
        }
        NotificationEvent event = new NotificationEvent();
        event.setEmail(order.getBuyerEmail());
        event.setType("INFO");
        event.setTitle("Order status updated");
        event.setMessage("Your order " + order.getOrderNumber() + " has been updated");
        this.serviceClient.sendNotification(event);
        return "order updated successfully";
    }

    private void updateParentOrderStatus(Long parentOrderId, OrderStatus newStatus) {
        Optional<Order> parentOrderOpt = this.orderRepository.findById(parentOrderId);
        if (parentOrderOpt.isEmpty()) {
            return;
        }
        Order parentOrder = parentOrderOpt.get();
        if (newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.SHIPPED) {
            parentOrder.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(parentOrder);
            return;
        }
        List<Order> childOrders = orderRepository.findByParentOrderId(parentOrderId);
        if (newStatus == OrderStatus.DELIVERED) {
            boolean allDelivered = childOrders.stream().allMatch(childOrder -> childOrder.getStatus() == OrderStatus.DELIVERED);
            if (allDelivered) {
                parentOrder.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(parentOrder);
            }
            return;
        }
        boolean allSameStatus = childOrders.stream().allMatch(childOrder -> childOrder.getStatus() == newStatus);
        if (allSameStatus) {
            parentOrder.setStatus(newStatus);
            orderRepository.save(parentOrder);
        }
    }

    private OrderAndItems findOrderAndItem(ProcessItemRequest request) {
        List<Order> orders = this.orderRepository.findAllByOrderNumber(request.getOrderNumber());
        for (Order order : orders) {
            List<OrderItem> items = order.getItems().stream().filter(orderItem -> request.getItemIds().contains(orderItem.getId())).toList();
            if (items.isEmpty()) continue;
            return new OrderAndItems(order, items);
        }
        throw new IllegalArgumentException("No order items found for order number: " + request.getOrderNumber() + " and item IDs: " + String.valueOf(request.getItemIds()));
    }

    public Double getShopPayoutAmount(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
        log.info("Shop payout amount:{}", shop.getTotalPayoutAmount());
        return shop.getTotalPayoutAmount().doubleValue();
    }

    public ShopStatistic getShopOrderStatistics(Long shopId) {
        shopRepository.findById(shopId).orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
        List<Order> shopOrders = this.orderRepository.findByShopIdAndIsParentOrderFalse(shopId);
        Long allOrderCount = (long) shopOrders.size();
        Long pendingOrderCount = shopOrders.stream().filter(order -> order.getStatus() == OrderStatus.PENDING).count();
        Long processingOrderCount = shopOrders.stream().filter(order -> order.getStatus() == OrderStatus.PROCESSING).count();
        Long shippedOrderCount = shopOrders.stream().filter(order -> order.getStatus() == OrderStatus.SHIPPED).count();
        Long deliveredOrderCount = shopOrders.stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).count();
        ShopStatistic shopStatistic = new ShopStatistic();
        shopStatistic.setTotalOrderCount(allOrderCount);
        shopStatistic.setDeliveredOrderCount(deliveredOrderCount);
        shopStatistic.setPendingOrderCount(pendingOrderCount);
        shopStatistic.setProcessingOrderCount(processingOrderCount);
        shopStatistic.setShippedOrderCount(shippedOrderCount);
        return shopStatistic;
    }

    public Double getMarketTransactionAmount(Long marketId) {
        List<OrderResponse> shopOrders = this.getMarketOrders(marketId);
        return shopOrders.stream().mapToDouble(orderResponse -> orderResponse.getTotalAmount().doubleValue()).sum();
    }

    private List<OrderResponse> getMarketOrders(Long marketId) {
        this.marketRepository.findById(marketId).orElseThrow(() -> new ResourceNotFoundException("market not found with ID:"));
        List<Order> marketOrders = this.orderRepository.findByMarketAndIsParentOrderTrue(marketId);
        log.info("Market orders:{} ", (Object)marketOrders);
        if (marketOrders.isEmpty()) {
            return List.of();
        }
        return marketOrders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }

    public Long getMarketTransactionCount(Long marketId) {
        List<OrderResponse> marketOrders = getMarketOrders(marketId);
        return (long) marketOrders.size();
    }
}