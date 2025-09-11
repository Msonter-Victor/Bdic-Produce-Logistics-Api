package dev.gagnon.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String fromEmail;
    private String toEmail;
    private String message;
}