package dev.gagnon.Controller;

import dev.gagnon.DTO.ApiResponse;
import dev.gagnon.DTO.ApiResponse2;
import dev.gagnon.DTO.ProductDto;
import dev.gagnon.DTO.ProductResponseDto;
import dev.gagnon.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @ModelAttribute ProductDto dto,
            @RequestPart(value = "mainImageUrl", required = false) MultipartFile mainImageUrl,
            @RequestPart(value = "sideImage1Url", required = false) MultipartFile sideImage1Url,
            @RequestPart(value = "sideImage2Url", required = false) MultipartFile sideImage2Url,
            @RequestPart(value = "sideImage3Url", required = false) MultipartFile sideImage3Url,
            @RequestPart(value = "sideImage4Url", required = false) MultipartFile sideImage4Url
    ) {
        ApiResponse<ProductDto> response = productService.createProduct(
                dto, mainImageUrl, sideImage1Url, sideImage2Url, sideImage3Url, sideImage4Url
        );
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 400)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id) {
        ProductResponseDto response = productService.getProductById(id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllProducts() {
        String response = productService.deleteAllProducts();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse2<List<ProductResponseDto>>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> update(
            @PathVariable Long id,
            @RequestBody ProductDto dto
    ) {
        ApiResponse<ProductDto> response = productService.updateProduct(id, dto);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 404)
                .body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        ApiResponse<Void> response = productService.deleteProduct(id);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 404)
                .body(response);
    }

    @GetMapping("/product/image/{filename:.+}")
    public ResponseEntity<?> getProductImage(@PathVariable String filename) {
        try {
            // Make sure this matches the actual path where your files are saved
            String uploadDir = "/var/www/bdic_virtual_market_BackEnd/uploads/";
            File imageFile = new File(uploadDir + filename);
            if (!imageFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
            }
            String contentType = Files.probeContentType(imageFile.toPath());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"" + imageFile.getName() + "\"")
                    .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                    .body(Files.readAllBytes(imageFile.toPath()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading image: " + e.getMessage());
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
                .status(response.isSuccess() ? 200 : 404)
                .body(response);
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        ApiResponse<Void> response = productService.updateProductQuantity(id, quantity);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 404)
                .body(response);
    }

}
