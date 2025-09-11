package dev.gagnon.dto;

import lombok.Data;

@Data
public class MarkReadRequest {
    private String fromEmail;
    private String toEmail;
}