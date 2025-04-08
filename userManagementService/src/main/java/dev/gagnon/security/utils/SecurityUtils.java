package dev.gagnon.security.utils;

import java.util.List;

public class SecurityUtils {
    private SecurityUtils() {}

    public static final String JWT_PREFIX = "Bearer ";

    public static final List<String>
            PUBLIC_ENDPOINTS = List.of(
            "/api/v1/auth/**",
            "/api/v1/users/register",
            "/api/v1/farmer/findBy/{email}",
            "/api/v1/logistics/register",
            "/api/v1/buyer/register"
    );




}
