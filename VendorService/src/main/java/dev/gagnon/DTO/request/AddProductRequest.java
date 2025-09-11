package dev.gagnon.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddProductRequest {
    private String vendorEmail;
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer quantity;

    private MultipartFile mainImageUrl;
    private MultipartFile sideImage1Url;
    private MultipartFile sideImage2Url;
    private MultipartFile sideImage3Url;
    private MultipartFile sideImage4Url;

    private Long shopId;
    private Long categoryId;
    private Long subCategoryId;
}
