package dev.gagnon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResult {
    private Long productId;
    private String productName;
    private String description;
    private String category;
    private String subCategory;
    private String market;
    private String shopName;
    private Long shopId;
    private double similarity;
}