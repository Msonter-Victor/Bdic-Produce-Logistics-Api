package dev.gagnon.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private String fromEmail;
    private String toEmail;
    private String message;
    private Long productId;
    private Long shopId;
    private String productName;
    private String shopName;
}