package dev.gagnon.security.utils;

import java.util.List;

public class SecurityUtils {

    private SecurityUtils() {}

    public static final String JWT_PREFIX = "Bearer ";

    public static final List<String>
            PUBLIC_ENDPOINTS = List.of(
                "/api/auth/login",
                "/api/auth/logout",
                "/api/users/add-role",
                "/api/users/resend-verification",
                "/api/users/register",
                "/api/auth/test-email",
                "/api/users/verify",
                "/api/auth/forgot-password",
                "api/users/dashboard-redirect",
                "/api/auth/reset-password"
    );

}
