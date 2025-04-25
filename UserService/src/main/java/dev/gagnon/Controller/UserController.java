package dev.gagnon.Controller;
import dev.gagnon.DTO.UserRegistrationRequest;
import dev.gagnon.Service.FileService;
import dev.gagnon.Service.UserService;
import dev.gagnon.Util.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    private FileService fileService;
//--------------------------------------------------------------------------------------------------
@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> register(
        @ModelAttribute UserRegistrationRequest request,
        @RequestPart(required = false) MultipartFile passport
) {
    // Optionally handle the file if present
    String passportUrl = null;
    if (passport != null && !passport.isEmpty()) {
        // save the file or get its URL
        passportUrl = fileService.save(passport); // hypothetical service
    }
    // inject the file URL into the request object
    request.setPassportUrl(passportUrl);
//    User newUser = userService.registerUser(request);
//    return ResponseEntity.ok(newUser);
    return userService.registerUser(request);
}


//--------------------------------------------------------------------------------------------------
//    @GetMapping("/verify")
//    public void verifyUser(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
//        try {
//            userService.verifyUser(token);
//            response.sendRedirect("https://marketplace.bdic.ng/register/getStarted"); // ✅ success
//        } catch (RuntimeException ex) {
//            String errorMessage = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
//            response.sendRedirect("https://marketplace.bdic.ng/verification-error?error=" + errorMessage);
//        }
//    }
//--------------------------------------------------------------------------------------------------------------
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendEmail(@RequestParam String email) {
        try {
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email resent.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
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
    if (roles.contains("LOGISTICS")) dashboards.put("LOGISTICS", "/logistics/dashboard");

    return dashboards.isEmpty()
            ? ResponseEntity.status(HttpStatus.FORBIDDEN).body("No accessible dashboards")
            : ResponseEntity.ok(dashboards);
}

}
