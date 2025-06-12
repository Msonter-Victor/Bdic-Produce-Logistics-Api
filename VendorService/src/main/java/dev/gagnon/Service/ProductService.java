package dev.gagnon.Service;


import dev.gagnon.DTO.*;
import dev.gagnon.Model.Product;
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

<<<<<<< HEAD
    boolean checkProductAvailability(Long id, Integer quantity);

    ApiResponse<Void> updateProductStock(Long id, Integer quantity);

    ApiResponse<Void> updateProductQuantity(Long id, Integer quantity);
=======
    //ApiResponse2<List<ProductResponseDto>> searchProducts(String categoryName, String shopName, String marketName);

    //List<ProductSearchResponseDTO> searchProducts(ProductSearchRequest request);

    // ProductService.java
    ApiResponse2<List<ProductSearchResponseDTO>> searchProducts(ProductSearchRequest request);


>>>>>>> dev
}
