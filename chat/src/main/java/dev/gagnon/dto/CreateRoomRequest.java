package dev.gagnon.dto;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String buyerUsername;
    private String sellerUsername;
    private Long productId;
}