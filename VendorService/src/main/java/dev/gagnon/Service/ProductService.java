package dev.gagnon.Service;


import dev.gagnon.DTO.ApiResponse;
import dev.gagnon.DTO.ApiResponse2;
import dev.gagnon.DTO.ProductDto;
import dev.gagnon.DTO.ProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ApiResponse<ProductDto> createProduct(
            ProductDto dto,
            MultipartFile mainImage,
            MultipartFile sideImage1,
            MultipartFile sideImage2,
            MultipartFile sideImage3,
            MultipartFile sideImage4
    );

    ProductResponseDto getProductById(Long id);

    ApiResponse2<List<ProductResponseDto>> getAllProducts();

    ApiResponse<ProductDto> updateProduct(Long id, ProductDto dto);

    ApiResponse<Void> deleteProduct(Long id);

    boolean checkProductAvailability(Long id, Integer quantity);

    ApiResponse<Void> updateProductStock(Long id, Integer quantity);

    ApiResponse<Void> updateProductQuantity(Long id, Integer quantity);
}
