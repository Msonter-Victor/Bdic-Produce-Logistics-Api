package dev.gagnon.Service.Implementation;

import com.cloudinary.Cloudinary;
import dev.gagnon.DTO.ApiResponse;
import dev.gagnon.DTO.ApiResponse2;
import dev.gagnon.DTO.ProductDto;
import dev.gagnon.DTO.ProductResponseDto;
import dev.gagnon.Exception.BusinessException;
import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Model.Category;
import dev.gagnon.Model.Product;
import dev.gagnon.Model.Shop;
import dev.gagnon.Repository.CategoryRepository;
import dev.gagnon.Repository.ProductRepository;
import dev.gagnon.Repository.ShopRepository;
import dev.gagnon.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.gagnon.Util.ServiceUtils.getMediaUrl;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final ShopRepository shopRepo;
    private final CategoryRepository categoryRepo;
    private final Cloudinary cloudinary;
// Inject from properties file
// @Value("${upload.dir}")
// private String uploadDir;
    private final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "//uploads//").toString();

    @Override
    public ApiResponse<ProductDto> createProduct(ProductDto dto,
                                                 MultipartFile mainImage,
                                                 MultipartFile sideImage1,
                                                 MultipartFile sideImage2,
                                                 MultipartFile sideImage3,
                                                 MultipartFile sideImage4) {

        Shop shop = shopRepo.findById(dto.getShopId()).orElse(null);
        Category category = categoryRepo.findById(dto.getCategoryId()).orElse(null);
        if (shop == null || category == null) {
            return new ApiResponse<>(false, "Invalid shop or category", null);
        }
        // Upload images and get URLs
        String mainImageUrl = getMediaUrl(mainImage, cloudinary.uploader());
        String sideImage1Url = getMediaUrl(sideImage1, cloudinary.uploader());
        String sideImage2Url = getMediaUrl(sideImage2, cloudinary.uploader());
        String sideImage3Url = getMediaUrl(sideImage3, cloudinary.uploader());
        String sideImage4Url = getMediaUrl(sideImage4, cloudinary.uploader());

        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .quantity(dto.getQuantity())
                .mainImageUrl(mainImageUrl)
                .sideImage1Url(sideImage1Url)
                .sideImage2Url(sideImage2Url)
                .sideImage3Url(sideImage3Url)
                .sideImage4Url(sideImage4Url)
                .shop(shop)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product saved = productRepo.save(product);

        ProductDto responseDto = mapToDto(saved);
        return new ApiResponse<>(true, "Product created successfully", responseDto);
    }


    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("product not found"));
        return mapToDto4SingleProduct(product);
    }

    @Override
    public ApiResponse2<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> list = productRepo.findAll()
                .stream()
                .map(this::mapToDto4SingleProduct)
                .collect(Collectors.toList());
        return new ApiResponse2<>(true, "Fetched all products", list);
    }

    @Override
    public ApiResponse<ProductDto> updateProduct(Long id, ProductDto dto) {
        return productRepo.findById(id).map(product -> {
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
//            product.setMainImageUrl(dto.getMainImageUrl());
//            product.setSideImage1Url(dto.getSideImage1Url());
//            product.setSideImage2Url(dto.getSideImage2Url());
//            product.setSideImage3Url(dto.getSideImage3Url());
//            product.setSideImage4Url(dto.getSideImage4Url());
            product.setUpdatedAt(LocalDateTime.now());

            productRepo.save(product);
            return new ApiResponse<>(true, "Product updated", mapToDto(product));
        }).orElse(new ApiResponse<>(false, "Product not found", null));
    }
    @Override
    public ApiResponse<Void> deleteProduct(Long id) {
        if (!productRepo.existsById(id)) {
            return new ApiResponse<>(false, "Product not found", null);
        }
        productRepo.deleteById(id);
        return new ApiResponse<>(true, "Product deleted", null);
    }

    public boolean checkProductAvailability(Long productId, Integer requestedQuantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return product.getQuantity() >= requestedQuantity;
    }

    public ApiResponse<Void> updateProductStock(Long productId, Integer quantityChange) {
        try {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            int newQuantity = product.getQuantity() + quantityChange;
            if (newQuantity < 0) {
                throw new ResourceNotFoundException("Insufficient stock available");
            }

            product.setQuantity(newQuantity);
            productRepo.save(product);

            return new ApiResponse<>(true, "Stock updated successfully", null);
        } catch (ResourceNotFoundException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<Void> updateProductQuantity(Long productId, Integer newQuantity) {
        try {
            if (newQuantity < 0) {
                throw new BusinessException("Quantity cannot be negative");
            }

            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            product.setQuantity(newQuantity);
            productRepo.save(product);

            return new ApiResponse<>(true, "Quantity updated successfully", null);
        } catch (ResourceNotFoundException | BusinessException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    @Override
    public String deleteAllProducts() {
        productRepo.deleteAll();
        return "successfully deleted products";
    }

    //    private String saveImage(MultipartFile file) {
//        if (file == null || file.isEmpty()) return null;
//
//        try {
//            File dir = new File(UPLOAD_DIR);
//            if (!dir.exists()) dir.mkdirs();
//
//            String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            File destFile = new File(dir, uniqueFilename);
//            file.transferTo(destFile);
//
//            // Return path relative to application root for front-end use
//            return "/uploads/" + uniqueFilename;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();
            // Sanitize the original filename by replacing spaces with underscores
            String originalFilename = file.getOriginalFilename().replaceAll("\\s+", "_");

            // Optionally, you could also strip out other special characters if needed:
            // originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "");
            String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
            File destFile = new File(dir, uniqueFilename);
            file.transferTo(destFile);

            // Return the relative path (for front-end use)
            return "/uploads/" + uniqueFilename;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());
//        dto.setMainImageUrl(product.getMainImageUrl());
//        dto.setSideImage1Url(product.getSideImage1Url());
//        dto.setSideImage2Url(product.getSideImage2Url());
//        dto.setSideImage3Url(product.getSideImage3Url());
//        dto.setSideImage4Url(product.getSideImage4Url());
//        dto.setShopId(product.getShop().getId());
//        dto.setCategoryId(product.getCategory().getId());
        return dto;
    }

   //String BASE_URL = "C:/Users/BDIC/IdeaProjects/Bdic-Produce-Logistics-Api";

    private ProductResponseDto mapToDto4SingleProduct(Product product) {
        ProductResponseDto dto = new ProductResponseDto();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(BigDecimal.valueOf(product.getPrice()));
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());

        // Set image URLs
        dto.setMainImageUrl(product.getMainImageUrl());
        dto.setSideImage1Url(product.getSideImage1Url());
        dto.setSideImage2Url(product.getSideImage2Url());
        dto.setSideImage3Url(product.getSideImage3Url());
        dto.setSideImage4Url(product.getSideImage4Url());

        // Set shop info
        dto.setShopId(product.getShop().getId());
        dto.setShopName(product.getShop().getName());

        // Set category info
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());

        return dto;
    }



}
