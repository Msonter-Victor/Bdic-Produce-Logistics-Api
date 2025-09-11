package dev.gagnon.Service;

import dev.gagnon.Model.User;
import dev.gagnon.Model.VerificationToken;

public interface OTPService {
    String generateOTP();
    VerificationToken createOTPToken(User user);
    boolean validateOTP(String email, String otp);
    boolean resendOTP(String email);
    int getRemainingAttempts(String email);
    boolean isOTPExpired(String email);
    
    // Forgot password methods
    VerificationToken createPasswordResetOTPToken(User user);
    boolean validatePasswordResetOTP(String email, String otp);
    boolean resendPasswordResetOTP(String email);
}