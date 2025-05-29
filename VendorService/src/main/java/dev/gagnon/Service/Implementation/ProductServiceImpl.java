package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.ApiResponse;
import dev.gagnon.DTO.ApiResponse2;
import dev.gagnon.DTO.ProductDto;
import dev.gagnon.DTO.ProductResponseDto;
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
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final ShopRepository shopRepo;
    private final CategoryRepository categoryRepo;
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
        String mainImageUrl = saveImage(mainImage);
        String sideImage1Url = saveImage(sideImage1);
        String sideImage2Url = saveImage(sideImage2);
        String sideImage3Url = saveImage(sideImage3);
        String sideImage4Url = saveImage(sideImage4);

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
    public ApiResponse2<ProductResponseDto> getProductById(Long id) {
        return productRepo.findById(id)
                .map(product -> new ApiResponse2<>(true, "Success", mapToDto4SingleProduct(product)))
                .orElse(new ApiResponse2<>(false, "Product not found", null));
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
        dto.setShopId(product.getShop().getId());
        dto.setCategoryId(product.getCategory().getId());
        return dto;
    }

   //String BASE_URL = "C:/Users/BDIC/IdeaProjects/Bdic-Produce-Logistics-Api";

    String BASE_URL = "https://api.digitalmarke.bdic.ng";
    private ProductResponseDto mapToDto4SingleProduct(Product product) {
        ProductResponseDto dto = new ProductResponseDto();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());

        // Set image URLs
        dto.setMainImageUrl(BASE_URL + product.getMainImageUrl());
        dto.setSideImage1Url(BASE_URL + product.getSideImage1Url());
        dto.setSideImage2Url(BASE_URL + product.getSideImage2Url());
        dto.setSideImage3Url(BASE_URL + product.getSideImage3Url());
        dto.setSideImage4Url(BASE_URL + product.getSideImage4Url());

        // Set shop info
        dto.setShopId(product.getShop().getId());
        dto.setShopName(product.getShop().getName());

        // Set category info
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());

        return dto;
    }

}
