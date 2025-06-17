package dev.gagnon.dto.response;

public enum OrderStatus {
    PENDING,
    IN_TRANSIT,
    PICKED_UP,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}