package dev.gagnon.Controller;
import dev.gagnon.DTO.CartDTO;
import dev.gagnon.Model.User;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.CartService;
import dev.gagnon.Util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping("/fetch")
    public ApiResponse<CartDTO> getCart(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        CartDTO cart = cartService.getCartByUserId(user.getId());
        if (cart == null) {
            return new ApiResponse<>(false, "No cart found for this user", null);
        }
        return new ApiResponse<>(true, "Cart retrieved successfully", cart);
    }


}
