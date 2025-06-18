package dev.gagnon.DTO;

import jakarta.validation.constraints.Null;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
@Null
@Data
public class ProductDto {
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
}
