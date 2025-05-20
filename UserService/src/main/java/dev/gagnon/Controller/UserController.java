package dev.gagnon.Controller;
import dev.gagnon.DTO.AuthRequest;
import dev.gagnon.DTO.AuthResponse;
import dev.gagnon.DTO.UserRegistrationRequest;
import dev.gagnon.Model.Role;
import dev.gagnon.Model.User;
import dev.gagnon.Repository.RoleRepository;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.EmailService;
import dev.gagnon.Service.FileService;
import dev.gagnon.Service.JwtService;
import dev.gagnon.Service.UserService;
import dev.gagnon.Util.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Autowired
    private FileService fileService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
//--------------------------------------------------------------------------------------------------
@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> register(
        @ModelAttribute UserRegistrationRequest request
//        @RequestPart(required = false) MultipartFile passport
) {
//    // Optionally handle the file if present
//    String passportUrl = null;
//    if (passport != null && !passport.isEmpty()) {
//        // save the file or get its URL
//        passportUrl = fileService.save(passport); // hypothetical service
//    }
//    // inject the file URL into the request object
//    request.setPassportUrl(passportUrl);
////    User newUser = userService.registerUser(request);
////    return ResponseEntity.ok(newUser);
    return userService.registerUser(request);
}
//--------------------------------------------------------------------------------------------------------------
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

    // Check rate limit
    LocalDateTime now = LocalDateTime.now();
    if (user.getLastVerificationSentAt() != null &&
            Duration.between(user.getLastVerificationSentAt(), now).toSeconds() < 60) {
        long secondsLeft = 60 - Duration.between(user.getLastVerificationSentAt(), now).toSeconds();
        return ResponseEntity.status(429)
                .body("Please wait " + secondsLeft + " seconds before requesting another email.");
    }

    // Generate new token and expiration
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

    //-------------------------------------------------------------------------------------------------------
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

        // Any other error (e.g., token invalid or tampered)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Verification failed: " + errorMessage
        ));
    }
}
//------------------------------------------------------------------------------------------
@GetMapping("/dashboard-redirect")
public ResponseEntity<?> getAccessibleDashboards(@AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
    }

    List<String> roles = userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    Map<String, String> dashboards = new HashMap<>();
    if (roles.contains("ADMIN")) dashboards.put("ADMIN", "/admin/dashboard");
    if (roles.contains("VENDOR")) dashboards.put("VENDOR", "/vendor/dashboard");
    if (roles.contains("BUYER")) dashboards.put("BUYER", "/buyer/dashboard");

    return dashboards.isEmpty()
            ? ResponseEntity.status(HttpStatus.FORBIDDEN).body("No accessible dashboards")
            : ResponseEntity.ok(dashboards);
}
//---------------------------------------------------------------------------------------

@PostMapping("/add-role")
public ResponseEntity<?> addRoleToAuthenticatedUser(@RequestBody Map<String, String> requestBody) {
    String roleName = requestBody.get("name");

    // Get authenticated user's email/username from SecurityContext
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName(); // assuming email is the username

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Role role = roleRepository.findByName(roleName.toUpperCase());
    if (role == null) {
        return ResponseEntity.badRequest().body("Role not found.");
    }

    if (user.getRoles().contains(role)) {
        return ResponseEntity.ok("You already have the " + roleName + " role.");
    }

    user.getRoles().add(role);
    userRepository.save(user);

    return ResponseEntity.ok("Role '" + roleName + "' added successfully.");
}
//---------------------------------------------------------------------LOGIN
//@CrossOrigin(origins = "https://marketplace.bdic.ng", allowCredentials = "true")
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList();

        String token = jwtService.generateToken(userDetails, roles);

        return ResponseEntity.ok(new AuthResponse(token, roles));
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(401).body("Invalid email or password");
    } catch (DisabledException e) {
        return ResponseEntity.status(403).body("Account not verified, Check your email to verify");
    }
}

}
