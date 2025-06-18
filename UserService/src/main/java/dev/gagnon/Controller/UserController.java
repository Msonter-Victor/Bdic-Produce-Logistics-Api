package dev.gagnon.Controller;
import dev.gagnon.DTO.*;
import dev.gagnon.Model.User;
import dev.gagnon.Model.constants.Role;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.EmailService;
import dev.gagnon.Service.UserService;
import dev.gagnon.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@ModelAttribute UserRegistrationRequest request) {
        try{
            var response = userService.registerUser(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User with this email does not exist.");
        }
        User user = optionalUser.get();
        if (user.isVerified()) {
            return ResponseEntity.badRequest().body("Account already verified.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (user.getLastVerificationSentAt() != null &&
            Duration.between(user.getLastVerificationSentAt(), now).toSeconds() < 60) {
            long secondsLeft = 60 - Duration.between(user.getLastVerificationSentAt(), now).toSeconds();
            return ResponseEntity.status(429)
                .body("Please wait " + secondsLeft + " seconds before requesting another email.");
        }

        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setTokenExpiration(now.plusMinutes(15));
        user.setLastVerificationSentAt(now);
        userRepository.save(user);

        String verificationLink = "http://localhost:8982/api/users/verify?token=" + newToken;

        try {
            emailService.sendVerificationEmail(
                user.getEmail(),
                "Verify Your Account",
                "<p>Click the link to verify your account: <a href=\"" + verificationLink + "\">Verify</a></p>"
            );
            return ResponseEntity.ok("Verification email has been resent.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to resend verification email. Please try again later.");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        try {
        // ✅ Attempt verification
            userService.verifyUser(token);

        // Success - inform frontend to redirect to login
            return ResponseEntity.ok(Map.of(
                    "message", "Verification successful. Redirecting to login...",
                "redirectUrl", "https://marketplace.bdic.ng/register/getStarted"
            ));

        } catch (RuntimeException ex) {
            String errorMessage = ex.getMessage();

            if (errorMessage.contains("expired")) {
                try {
                    String email = userService.extractEmailFromToken(token);
                    userService.resendVerificationEmail(email);

                    return ResponseEntity.status(HttpStatus.GONE).body(Map.of(
                        "error", "Verification token expired. A new verification email has been sent to " + email
                    ));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "error", "Token expired and resend failed: " + errorMessage
                    ));
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Verification failed: " + errorMessage
            ));
        }
    }

    @PostMapping("/add-role")
    public ResponseEntity<?> addRoleToAuthenticatedUser(@RequestBody AddRoleRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.getRoles().add(Role.valueOf(request.getRoleName()));
        userRepository.save(user);

        return ResponseEntity.ok("Role '" + request.getRoleName() + "' added successfully.");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        List<User>users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
            .map(UserResponse::new)
            .toList();
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }
}
