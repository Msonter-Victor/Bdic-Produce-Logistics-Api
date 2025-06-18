package dev.gagnon.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
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

    private String vendor;
    private String market_section;
    private String shop_address;
    private String status;

    // New fields for counts
    private Long countByShop;
    private Long countByShopAndCategory;
    private Long countByCategory;
}
