package dev.gagnon.dto;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Long quantitySold;
    private String mainImageUrl;
    private String sideImage1Url;
    private String sideImage2Url;
    private String sideImage3Url;
    private String sideImage4Url;
    private String categoryName;
    private String subCategoryName;
    private String shopName;
    private Long shopId;
    private String marketName;
    private Long marketId;
    private String marketSection;
}