package dev.gagnon.Controller;

import dev.gagnon.Model.User;
import dev.gagnon.Service.UserService;
import dev.gagnon.Util.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/verify")
    public void verifyUser(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        try {
            userService.verifyUser(token);
            response.sendRedirect("https://marketplace.bdic.ng/register/getStarted"); // ✅ success
        } catch (RuntimeException ex) {
            String errorMessage = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect("https://marketplace.bdic.ng/verification-error?error=" + errorMessage);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendEmail(@RequestParam String email) {
        try {
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email resent.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
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
