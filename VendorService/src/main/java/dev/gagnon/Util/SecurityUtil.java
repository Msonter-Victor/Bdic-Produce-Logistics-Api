package dev.gagnon.Util;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        // Assumes principal is a custom UserDetails object or the ID itself.
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser().getId(); // your custom user class
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            // If only username is stored, you'll need to fetch user ID from DB
            throw new RuntimeException("User ID not available in principal");
        } else if (principal instanceof String userIdStr) {
            return Long.parseLong(userIdStr); // if JWT stores userId directly
        }

        throw new RuntimeException("Unable to extract user ID");
    }
}
