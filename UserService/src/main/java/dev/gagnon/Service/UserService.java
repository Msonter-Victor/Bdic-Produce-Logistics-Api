package dev.gagnon.Service;

import dev.gagnon.DTO.RegisterResponse;
import dev.gagnon.DTO.ResetPasswordRequest;
import dev.gagnon.Model.User;
import org.springframework.http.ResponseEntity;
import dev.gagnon.DTO.UserRegistrationRequest;
import org.springframework.stereotype.Service;


public interface UserService {
    boolean verifyUser(String token);
    RegisterResponse registerUser(UserRegistrationRequest request);

    void resendVerificationEmail(String email);

   String extractEmailFromToken(String token);
    //void resetPassword(String token, String newPassword);
    void resetPassword(ResetPasswordRequest dto);
    //void handleForgotPassword(String email);
    ResponseEntity<?> handleForgotPassword(String email);


    User findByEmail(String username);
}
