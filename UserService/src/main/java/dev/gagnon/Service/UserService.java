package dev.gagnon.Service;

import org.springframework.http.ResponseEntity;
import dev.gagnon.DTO.UserRegistrationRequest;
import org.springframework.stereotype.Service;


public interface UserService {
    boolean verifyUser(String token);
    ResponseEntity<?> registerUser(UserRegistrationRequest request);

    void resendVerificationEmail(String email);

   String extractEmailFromToken(String token);


}
