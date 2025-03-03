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
            "/api/v1/product/findAll",
            "/api/v1/product/findBy/{id}",
            "/api/v1/product/delete/{id}",
            "/api/v1/logistics/register",
            "/api/v1/buyer/register"
    );


    public static final String[] FARMER_ENDPOINTS = {
            "/api/v1/product/add",
            "/api/v1/buyer/addAccountDetails",
            "/api/v1/product/farmerProducts"
    };



    public static final String[] BUYER_ENDPOINTS = {
            "/api/v1/order/**",
            "/api/v1/order/addToOrder",
            "/api/v1/order/removeProductFromOrder",
            "/api/v1/logistics/findAvailableRiders",
            "/api/v1/logistics/confirmDelivery",
            "/api/v1/payment/transfer"
    };

    public static final String[] RIDER_ENDPOINTS = {
            "/api/v1/logistics/updateLocation",
            "/api/v1/logistics/sendDeliveryConfirmation"
    };
}
