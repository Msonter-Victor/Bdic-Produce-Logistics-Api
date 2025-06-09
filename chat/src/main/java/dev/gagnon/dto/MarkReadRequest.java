package dev.gagnon.dto;

import lombok.Data;

@Data
public class MarkReadRequest {
    private String roomId;
    private String username;
}