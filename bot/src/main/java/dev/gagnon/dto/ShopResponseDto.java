package dev.gagnon.dto;

import lombok.Data;

@Data
public class ShopResponseDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String marketSection;
    private String logoImageUrl;
    private String status;
    private String marketName;
    private Long marketId;
    private int productCount;
}