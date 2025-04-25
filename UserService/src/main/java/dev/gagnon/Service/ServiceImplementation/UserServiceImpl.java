package dev.gagnon.Service.ServiceImplementation;

import dev.gagnon.DTO.UserRegistrationRequest;
import dev.gagnon.Model.Role;
import dev.gagnon.Model.User;
import dev.gagnon.Repository.RoleRepository;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.EmailService;
import dev.gagnon.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

private final UserRepository userRepository;

private final EmailService emailService;

private final PasswordEncoder passwordEncoder;

private final RoleRepository roleRepository;

    //--------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<?> registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .surname(request.getSurname())
                .otherName(request.getOtherName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone() != null ? request.getPhone() : null)
                .nin(request.getNin() != null ? request.getNin() : null)
                .passportUrl(request.getPassportUrl() != null ? request.getPassportUrl() : null)
                .verificationToken(UUID.randomUUID().toString())
                .tokenExpiration(LocalDateTime.now().plusMinutes(15))
                .isVerified(false);

        Role defaultRole = roleRepository.findById(1L).orElse(null);
        if (defaultRole != null) {
            userBuilder.roles(Set.of(defaultRole));
        }

        User user = userBuilder.build();
        User savedUser = userRepository.save(user);

        String verificationLink = "http://localhost:8982/api/users/verify?token=" + savedUser.getVerificationToken();
        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                "Verify Your Account",
                "<p>Click the link to verify your account: <a href=\"" + verificationLink + "\">Verify</a></p>"
        );

       return ResponseEntity.ok("User registered successfully. Please check your email for verification.");
        //return savedUser;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean verifyUser(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Verification failed: Invalid verification token.");
        }

        User user = userOpt.get();

        if (user.isVerified()) {
            throw new RuntimeException("Account already verified.");
        }

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            resendVerificationEmail(user.getEmail()); // 📨 Automatically resend
            throw new RuntimeException("Verification token expired. A new email has been sent to " + user.getEmail());
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);

        return true;
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (user.isVerified()) {
            throw new RuntimeException("This account has already been verified.");
        }
        // Check if token is missing, invalid or expired
        boolean shouldResend = false;

        if (user.getVerificationToken() == null || user.getTokenExpiration() == null) {
            shouldResend = true;
        } else if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            shouldResend = true;
        }

        if (!shouldResend) {
            throw new RuntimeException("A valid verification token already exists. Please check your email.");
        }
        // Generate new token
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String verificationLink = "http://localhost:8982/api/users/verify?token=" + user.getVerificationToken();
        emailService.sendVerificationEmail(
                user.getEmail(),
                "Verify Your Account",
                "<p>Click the link to verify your account: <a href=\"" + verificationLink + "\">Verify</a></p>"
        );
    }
    //----------------------------------------------------------------------------------------
    public String extractEmailFromToken(String token) {
        return userRepository.findByVerificationToken(token)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
    }


}
