package dev.gagnon.dto.response;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private Integer quantity;

    private String mainImageUrl;
    private String sideImage1Url;
    private String sideImage2Url;
    private String sideImage3Url;
    private String sideImage4Url;

    private Long shopId;
    private String shopName;

    private Long categoryId;
    private String categoryName;
}
