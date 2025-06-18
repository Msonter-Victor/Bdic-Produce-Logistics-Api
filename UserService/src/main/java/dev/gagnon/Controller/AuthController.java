package dev.gagnon.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.gagnon.DTO.*;
import dev.gagnon.Model.User;
import dev.gagnon.Service.EmailService;
import dev.gagnon.Service.UserService;
import dev.gagnon.security.config.RsaKeyProperties;
import dev.gagnon.security.data.models.SecureUser;
import dev.gagnon.security.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import static dev.gagnon.security.utils.SecurityUtils.JWT_PREFIX;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserService userService;
    private final AuthService authService;
    private final RsaKeyProperties rsaKeys;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(AUTHORIZATION) String token) {
        token = token.replace(JWT_PREFIX, "").strip();
        authService.blacklist(token);
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail().toLowerCase(),
                            loginRequest.getPassword()
                    )
            );

            SecureUser userDetails = (SecureUser) authentication.getPrincipal();
            String token = generateToken(authentication);

            Cookie cookie = createCookie(token);
            response.addCookie(cookie);

            LoginResponse loginResponse = LoginResponse.builder()
                    .responseTime(now())
                    .message("Login successful")
                    .firstName(userDetails.getFirstName())
                    .lastName(userDetails.getLastName())
                    .email(userDetails.getUsername())
                    .mediaUrl(userDetails.getMediaUrl())
                    .roles(userDetails.getRoles())
                    .token(token)
                    .build();

            return ResponseEntity.ok(new UserApiResponse<>(true, loginResponse));
        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .responseTime(now())
                    .isSuccessful(false)
                    .error("UnsuccessfulAuthentication")
                    .message("Invalid email or password")
                    .path("/api/auth/login")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    private String generateToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.RSA512(rsaKeys.publicKey(), rsaKeys.privateKey());
        Instant now = Instant.now();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        return JWT.create()
                .withIssuer("BDICAuthToken")
                .withIssuedAt(now)
                .withExpiresAt(now.plus(24, HOURS))
                .withSubject(principal.getUsername())
                .withClaim("principal", principal.getUsername())
                .withClaim("credentials", authentication.getCredentials().toString())
                .withArrayClaim("roles", extractAuthorities(authentication.getAuthorities()))
                .sign(algorithm);
    }

    private String[] extractAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    private static Cookie createCookie(String token) {
        Cookie cookie = new Cookie("BDICAuthToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        cookie.setSecure(true);
        return cookie;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        String email = principal.getName();
        log.info("name:{}",email);
        User user = userService.findByEmail(email);

        return ResponseEntity.ok(Map.of(
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail()
        ));
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        try {
            emailService.sendVerificationEmail(
                    "bemgbautor@gmail.com",
                    "Test Email",
                    "<p>This is a test from Mailgun</p>"
            );
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Email failed: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.handleForgotPassword(request.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}