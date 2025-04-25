package dev.gagnon.Controller;

import dev.gagnon.DTO.AuthRequest;
import dev.gagnon.DTO.AuthResponse;
import dev.gagnon.Service.EmailService;
import dev.gagnon.Service.JwtService;
import dev.gagnon.Util.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

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

}

