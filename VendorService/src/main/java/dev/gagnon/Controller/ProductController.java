package dev.gagnon.Controller;

import dev.gagnon.DTO.*;
import dev.gagnon.DTO.request.AddProductRequest;
import dev.gagnon.DTO.request.GetSubCategoryProductRequest;
import dev.gagnon.DTO.response.*;
import dev.gagnon.Exception.VendorBaseException;
import dev.gagnon.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @ModelAttribute AddProductRequest request) {
        AddProductResponse response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllProducts() {
        String response = productService.deleteAllProducts();
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponse2<List<ProductResponse>>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> update(
            @PathVariable Long id,
            @RequestBody ProductDto dto
    ) {
        ApiResponse<ProductDto> response = productService.updateProduct(id, dto);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 400)
                .body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        ApiResponse<Void> response = productService.deleteProduct(id);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 400)
                .body(response);
    }

    @GetMapping("/by-category")
    public ResponseEntity<?> getProductsByCategory(@RequestParam Long categoryId) {
        try{
            var response = productService.getAllProductsByCategory(categoryId);
            return ResponseEntity.ok().body(response);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/by-subcategoryName")
    public ResponseEntity<?> getProductsBySubCategory(@RequestParam String subCategoryName) {
        try{
            var response = productService.getAllProductsBySubCategoryName(subCategoryName);
            return ResponseEntity.ok().body(response);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/by-subcategory")
    public ResponseEntity<?> getProductsBySubCategoryName(@RequestParam Long subCategoryId) {
        try{
            var response = productService.getAllProductsBySubCategory(subCategoryId);
            return ResponseEntity.ok().body(response);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/getByUser")
    public ResponseEntity<?> getAllUserProducts(@RequestParam String email) {
        try{
            var response = productService.getAllUserProducts(email);
            return new ResponseEntity<>(response, OK);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("getUserProductCount")
    public ResponseEntity<?> getUserProductCount(@RequestParam String email) {
        try{
            var response = productService.getAllUserProductsCount(email);
            return new ResponseEntity<>(response, OK);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("getCategoryCount")
    public ResponseEntity<?> getCategoryProductsCount(@RequestParam Long id) {
        try{
            var response = productService.getAllProductsByCategoryCount(id);
            return new ResponseEntity<>(response, OK);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/update-price")
    public ResponseEntity<?> updatePrice(@RequestParam Long productId, @RequestParam Double price) {
        try{
            String response = productService.updatePrice(productId,price);
            return ResponseEntity.ok().body(response);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        boolean isAvailable = productService.checkProductAvailability(id, quantity);
        return ResponseEntity.ok(isAvailable);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        ApiResponse<Void> response = productService.updateProductStock(id, quantity);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 400)
                .body(response);
    }

    @PutMapping("/{id}/totalStock")
    public ResponseEntity<ApiResponse<Void>> updateTotalStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        ApiResponse<Void> response = productService.updateProductTotalStock(id, quantity);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 400)
                .body(response);
    }

    @GetMapping("/get-featured")
    public ResponseEntity<?> getFeaturedProducts() {
        try{
            List<ProductResponse>response = productService.getAllFeaturedProducts();
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/get-floated")
    public ResponseEntity<?> getFloatedProducts() {
        try{
            List<ProductResponse>response = productService.getAllFloatedProducts();
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/get-promoted")
    public ResponseEntity<?> getPromotedProducts() {
        try{
            List<ProductResponse>response = productService.getAllPromotedProducts();
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/get-user-featured")
    public ResponseEntity<?> getUserFeaturedProducts(@RequestParam Long shopId) {
        try{
            List<ProductResponse>response = productService.getShopFeaturedProducts(shopId);
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }



    @PutMapping("/feature")
    public ResponseEntity<?> featureProduct(@RequestParam Long productId) {
        try{
            String response = productService.featureProduct(productId);
            return new ResponseEntity<>(response, OK);
        }catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/promote")
    public ResponseEntity<?> promoteProduct(@RequestParam Long productId) {
        try{
            String response = productService.promote(productId);
            return new ResponseEntity<>(response, OK);
        }catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/float")
    public ResponseEntity<?> floatProduct(@RequestParam Long productId) {
        try{
            String response = productService.floatProduct(productId);
            return new ResponseEntity<>(response, OK);
        }catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        ApiResponse<Void> response = productService.updateProductQuantity(id, quantity);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 400)
                .body(response);
    }

    @GetMapping("/getBestSelling")
    public ResponseEntity<?> getBestSelling(@RequestParam Long shopId) {
        Optional<ProductResponseDto> product = productService.getBestSellingProduct(shopId);
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("/search-product")
    public ResponseEntity<?> searchProduct(@RequestParam String keyword) {
        List<ProductResponseDto> products = productService.search(keyword);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/searchProductByCategory")
    public ResponseEntity<?> searchProductByCategory(@RequestParam String categoryName) {
        List<ProductResponseDto> products = productService.searchByCategory(categoryName);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/getShopStockCount")
    public ResponseEntity<?> getShopStockCount(@RequestParam Long shopId){
        Long shopStock = productService.getTotalStockCount(shopId);
        return ResponseEntity.ok(shopStock);
    }

    @GetMapping("/getByMarket")
    public ResponseEntity<?> getByMarket(@RequestParam Long marketId){
        List<ProductResponseDto> products = productService.getAllByMarket(marketId);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/getByShop")
    public ResponseEntity<?> getByShop(@RequestParam Long shopId){
        try{
            List<ProductResponse>response = productService.getAllShopProducts(shopId);
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }


}
