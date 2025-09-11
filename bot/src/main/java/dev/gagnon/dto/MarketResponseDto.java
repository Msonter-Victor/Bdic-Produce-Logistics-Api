package dev.gagnon.dto;

import lombok.Data;
import java.util.List;

@Data
public class MarketResponseDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String status;
    private Integer lines;
    private List<ShopDto> shops;
    
    @Data
    public static class ShopDto {
        private Long id;
        private String name;
        private String description;
        private String address;
        private String marketSection;
        private String logoImageUrl;
        private String status;
    }
}