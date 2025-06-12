package dev.gagnon.Service.Implementation;
 import dev.gagnon.DTO.CartDTO;
 import dev.gagnon.DTO.CartItemDTO;
 import dev.gagnon.DTO.CartItemRequestDTO;
 import dev.gagnon.Model.Product;
 import dev.gagnon.Model.Status;
import dev.gagnon.Model.Cart;
import dev.gagnon.Model.CartItem;
import dev.gagnon.Repository.CartRepository;
 import dev.gagnon.Repository.ProductRepository;
 import dev.gagnon.Repository.StatusRepository;
 import dev.gagnon.Repository.UserRepository;
 import dev.gagnon.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Optional;
 import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final ProductRepository productRepository;

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getUser().getId());
        cartDTO.setStatusId(cart.getStatus().getId());
        cartDTO.setCreatedAt(cart.getCreatedAt());
        cartDTO.setUpdatedAt(cart.getUpdatedAt());

        cartDTO.setItems(
                cart.getItems().stream().map(this::mapToDTO).collect(Collectors.toList())
        );

        return cartDTO;
    }

    @Override
    public CartDTO addOrUpdateCartItem(Long userId, CartItemRequestDTO request) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found")));

            Status pendingStatus = statusRepository.findByName("PENDING")
                    .orElseThrow(() -> new RuntimeException("Default status 'PENDING' not found"));
            newCart.setStatus(pendingStatus);

            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        BigDecimal price = product.getPrice();
        BigDecimal discount = product.getDiscountAmount();
        BigDecimal discountedPrice = product.getDiscountedPrice();
        BigDecimal subtotal = discountedPrice.multiply(BigDecimal.valueOf(request.getQuantity()));


        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(request.getQuantity());
            item.setPrice(price);
            item.setDiscountAmount(discount);
            item.setSubtotal(subtotal);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setPrice(price);
            newItem.setDiscountAmount(discount);
            newItem.setSubtotal(subtotal);

            Status pendingItemStatus = statusRepository.findByName("PENDING")
                    .orElseThrow(() -> new RuntimeException("Default item status 'PENDING' not found"));
            newItem.setStatus(pendingItemStatus);

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return getCartByUserId(userId);
    }


    @Override
    public CartDTO removeCartItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);

        return getCartByUserId(userId);
    }
    @Override
    public BigDecimal calculateCartTotal(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private CartItemDTO mapToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setPrice(item.getPrice());
        dto.setDiscountAmount(item.getDiscountAmount());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        dto.setStatusId(item.getStatus() != null ? item.getStatus().getId() : null);
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }
}
