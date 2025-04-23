package dev.gagnon.Service;

import org.springframework.http.ResponseEntity;
import dev.gagnon.DTO.UserRegistrationRequest;

public interface UserService {
    boolean verifyUser(String token);
    ResponseEntity<?> registerUser(UserRegistrationRequest request);

    void resendVerificationEmail(String email);

    // 🔥 Add this new method
   // String extractEmailFromToken(String token);
    public String extractEmailFromToken(String token) {
        return userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or unknown token"))
                .getEmail();
    }

}
