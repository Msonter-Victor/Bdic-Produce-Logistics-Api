package dev.gagnon.Service;


import dev.gagnon.DTO.*;
import dev.gagnon.DTO.request.AddProductRequest;
import dev.gagnon.DTO.request.GetSubCategoryProductRequest;
import dev.gagnon.DTO.response.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    AddProductResponse createProduct(AddProductRequest request);

    ProductResponse getProductById(Long id);

    ApiResponse2<List<ProductResponse>> getAllProducts();

    ApiResponse<ProductDto> updateProduct(Long id, ProductDto dto);

    ApiResponse<Void> deleteProduct(Long id);

    boolean checkProductAvailability(Long id, Integer quantity);

    ApiResponse<Void> updateProductStock(Long id, Integer quantity);

    ApiResponse<Void> updateProductQuantity(Long id, Integer quantity);

    String deleteAllProducts();
    // ProductService.java
    Long getAllProductsByCategoryCount(Long categoryId);
    List<ProductResponseDto> getAllUserProducts(String email);

    List<ProductResponseDto> getAllProductsByCategory(Long categoryId);

    Long getAllUserProductsCount(String email);

    Optional<ProductResponseDto> getBestSellingProduct(Long shopId);

    List<ProductResponseDto> search(String keyword);

    List<ProductResponseDto> searchByCategory(String categoryName);

    Long getTotalStockCount(Long shopId);

    List<ProductResponseDto> getAllByMarket(Long marketId);

    List<ProductResponseDto> getAllProductsBySubCategory(Long subCategoryId);

    List<ProductResponseDto> getAllProductsBySubCategoryName(String request);

    String featureProduct(Long productId);

    List<ProductResponse> getAllFeaturedProducts();

    List<ProductResponse> getShopFeaturedProducts(Long shopId);

    String updatePrice(Long productId, Double price);

    ApiResponse<Void> updateProductTotalStock(Long id, Integer quantity);

    String promote(Long productId);

    String floatProduct(Long productId);

    List<ProductResponse> getAllFloatedProducts();

    List<ProductResponse> getAllPromotedProducts();

    List<ProductResponse> getAllShopProducts(Long shopId);
}
