package dev.gagnon.Service;

import dev.gagnon.DTO.request.*;
import dev.gagnon.DTO.response.*;
import dev.gagnon.Model.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;


public interface UserService {
    boolean verifyUser(String token);
    RegisterResponse registerUser(UserRegistrationRequest request) throws MessagingException, UnsupportedEncodingException;
    void resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException;
    
    // OTP-based verification methods
    boolean verifyUserWithOTP(String email, String otp);
    boolean resendOTP(String email);
    void sendVerificationOTP(String email);
    
    User findByEmail(String username);
    UserResponse getProfileFor(String email);
    String addAddress(AddressRequest request);
    AddressResponse getAddress(String email);
    String updateAddress(AddressRequest request);
    boolean addressExists(String email);
    String addRoleToUser(AddRoleRequest request);
    boolean isUserVerified(String email);
    String resetPassword(PasswordRequest passwordRequest);
    boolean existsById(Long userId);
    boolean existsByEmail(String email);

    String deleteUser(Long id);

    String disableUser(Long id);

    String registerAdmin(UserRegistrationRequest request);
    
    // Forgot password methods
    boolean sendPasswordResetOTP(String email);
    boolean verifyPasswordResetOTP(String email, String otp);
    boolean resetPasswordWithOTP(String email, String otp, String newPassword);

    String registerLogCompany(UserRegistrationRequest request);

    String registerRider(RiderRegistrationRequest request);

    String deleteUserByEmail(String email);
}
