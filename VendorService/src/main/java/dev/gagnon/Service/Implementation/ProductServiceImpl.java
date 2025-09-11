package dev.gagnon.Service.Implementation;

import com.cloudinary.Cloudinary;
import dev.gagnon.DTO.*;
import dev.gagnon.DTO.request.AddProductRequest;
import dev.gagnon.DTO.response.*;
import dev.gagnon.Exception.BusinessException;
import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Model.*;
import dev.gagnon.Model.constants.FeaturedStatus;
import dev.gagnon.Model.constants.MarketStatus;
import dev.gagnon.Repository.*;
import dev.gagnon.Service.ProductService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static dev.gagnon.Model.constants.MarketStatus.ACTIVE;
import static dev.gagnon.Model.constants.ShopStatus.VERIFIED;
import static dev.gagnon.Util.ServiceUtils.getMediaUrl;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final ShopRepository shopRepo;
    private final CategoryRepository categoryRepo;
    private final Cloudinary cloudinary;
    private final SubCategoryRepository subCategoryRepo;
    private final ReviewRepository reviewRepo;
    private final MarketRepository marketRepository;

    @Override
    public AddProductResponse createProduct(AddProductRequest request) {
        Shop shop = shopRepo.findById(request.getShopId()).orElse(null);
        Category category = categoryRepo.findById(request.getCategoryId()).orElse(null);
        SubCategory subCategory = subCategoryRepo.findById(request.getSubCategoryId()).orElse(null);
        if (shop == null || category == null || subCategory == null) {
            throw new ResourceNotFoundException("Shop or Category or sub category not found");
        }
        // Upload images and get URLs
        String mainImageUrl = getMediaUrl(request.getMainImageUrl(), cloudinary.uploader());
        String sideImage1Url = getMediaUrl(request.getSideImage1Url(), cloudinary.uploader());
        String sideImage2Url = getMediaUrl(request.getSideImage2Url(), cloudinary.uploader());
        String sideImage3Url = getMediaUrl(request.getSideImage3Url(), cloudinary.uploader());
        String sideImage4Url = getMediaUrl(request.getSideImage4Url(), cloudinary.uploader());
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .salesPrice(request.getPrice())
                .category(category)
                .subCategory(subCategory)
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .mainImageUrl(mainImageUrl)
                .sideImage1Url(sideImage1Url)
                .sideImage2Url(sideImage2Url)
                .sideImage3Url(sideImage3Url)
                .sideImage4Url(sideImage4Url)
                .quantitySold(0L)
                .featuredStatus(FeaturedStatus.NOT_FEATURED)
                .isFloated(false)
                .isPromoted(false)
                .shop(shop)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepo.save(product);
        AddProductResponse response = new AddProductResponse();
        response.setId(product.getId());
        response.setMessage("Added product successfully");
        return response;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepo.findByIdAndActiveShop(id,VERIFIED,ACTIVE,ACTIVE).orElseThrow(()->new ResourceNotFoundException("product not found"));

        ProductResponse response = new ProductResponse(product);
        List<Review> reviews = reviewRepo.findByProductId(id);
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(ReviewResponse::new)
                .toList();
        response.setReviews(reviewResponses);
        return response;
    }

    @Override
    public ApiResponse2<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> list = productRepo.findAllFromVerifiedVendors(VERIFIED, ACTIVE, ACTIVE)
                .stream()
                .filter(product ->
                        product.getQuantity() - product.getQuantitySold()>=1
                )
                .map(ProductResponse::new)
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
            productRepo.save(product);
            return new ApiResponse<>(true, "Product updated", mapToDto(product));
        }).orElse(new ApiResponse<>(false, "Product not found", null));
    }
    @Override
    public ApiResponse<Void> deleteProduct(Long id) {
        if (!productRepo.existsById(id)) {
            return new ApiResponse<>(false, "Product not found", null);
        }
        List<Review> reviews = reviewRepo.findByProductId(id);
        reviewRepo.deleteAll(reviews);
        productRepo.deleteById(id);
        return new ApiResponse<>(true, "Product deleted", null);
    }

    public boolean checkProductAvailability(Long productId, Integer requestedQuantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        long availableNumber = product.getQuantity() - product.getQuantitySold();
        return availableNumber >= requestedQuantity;
    }

    public ApiResponse<Void> updateProductStock(Long productId, Integer quantityChange) {
        try {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getQuantity()-product.getQuantitySold() <= 0) {
                throw new ResourceNotFoundException("Insufficient stock available");
            }
            product.setQuantitySold(product.getQuantitySold() + quantityChange);
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

            product.setQuantitySold(product.getQuantitySold() + newQuantity);
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

    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());
        return dto;
    }

    @Override
    public List<ProductResponseDto> getAllUserProducts(String email) {
        List<Product> products =  productRepo.findByUserEmail(email);
        if (products.isEmpty()) return List.of();
        return products.stream()
                .map(this::mapToDto4SingleProduct)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getAllProductsByCategory(Long categoryId) {
        List<Product> products = productRepo.findByCategory(categoryId,VERIFIED,ACTIVE,ACTIVE);
        if (products.isEmpty()) return List.of();
        return products.stream()
                .map(this::mapToDto4SingleProduct)
                .toList();
    }

    @Override
    public Long getAllUserProductsCount(String email) {
        List<Product> products =  productRepo.findByUserEmail(email);
        return (long) products.size();
    }

    @Override
    public Optional<ProductResponseDto> getBestSellingProduct(Long shopId) {
        List<Product> products = productRepo.findAllByShop(shopId);
        List<Product> soldProduct = products.stream()
                .filter(product -> product.getQuantitySold() > 0)
                .toList();
        return soldProduct.stream()
                .max(Comparator.comparingLong(Product::getQuantitySold))
                .map(this::mapToDto4SingleProduct);
    }

    @Override
    public List<ProductResponseDto> search(String keyword) {
        List<Product> products = productRepo.searchAllBy(keyword,VERIFIED,ACTIVE,ACTIVE);
        if(products.isEmpty())return List.of();
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1
                )
                .toList();
        return filtered.stream()
                .map(this::mapToDto4SingleProduct)
                .toList();
    }


    @Override
    public List<ProductResponseDto> searchByCategory(String categoryName) {
        List<Product> products = productRepo.searchAllByCategory(categoryName,ACTIVE,ACTIVE,VERIFIED);
        if(products.isEmpty())return List.of();
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1
                        )
                .toList();
        return filtered.stream()
                .map(this::mapToDto4SingleProduct)
                .toList();
    }

    @Override
    public Long getTotalStockCount(Long shopId) {
        List<Product> products = productRepo.findAllByShop(shopId);
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1)
                .toList();
        return filtered.stream()
                .mapToLong(product -> product.getQuantity() - product.getQuantitySold()).sum();
    }

    @Override
    public List<ProductResponseDto> getAllByMarket(Long marketId) {
        List<Product>products = productRepo.findByMarket(marketId);
        return getProductResponseDtos(products);
    }

    @NotNull
    private List<ProductResponseDto> getProductResponseDtos(List<Product> products) {
        if (products.isEmpty())return List.of();
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1
                && product.getShop().getStatus()==VERIFIED)
                .toList();
        return  filtered.stream()
                .map(this::mapToDto4SingleProduct)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getAllProductsBySubCategory(Long subCategoryId) {
        List<Product> products = productRepo.findBySubCategory(subCategoryId,ACTIVE,ACTIVE,VERIFIED);
        return getProductResponseDtos(products);
    }

    @Override
    public List<ProductResponseDto> getAllProductsBySubCategoryName(String request) {
        List<Product> products = productRepo.findBySubCategoryName(request,ACTIVE,ACTIVE,VERIFIED);
        return getProductResponseDtos(products);
    }

    @Override
    public String featureProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if(product.getFeaturedStatus()==FeaturedStatus.FEATURED)
            throw new BusinessException("Product already featured");
        product.setFeaturedStatus(FeaturedStatus.FEATURED);
        Shop shop = product.getShop();
        if(Objects.equals(shop.getFeaturedNumber(), shop.getShopTier().getFeaturedNumber()))
            throw new BusinessException("Exhausted featured products, consider upgrading");
        shop.setFeaturedNumber(shop.getFeaturedNumber() + 1);
        product.setFeaturedAt(LocalDateTime.now());
        productRepo.save(product);
        return "Product successfully featured";
    }

    @Override
    public List<ProductResponse> getAllFeaturedProducts() {
        List<Product>products = productRepo.findAllByFeatured(FeaturedStatus.FEATURED);
        if(products.isEmpty())return List.of();
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1)
                .toList();
        return filtered.stream()
                .map(ProductResponse::new)
                .toList();
    }

    @Override
    public List<ProductResponse> getShopFeaturedProducts(Long shopId) {
        List<Product>products = productRepo.findAllByFeaturedAndShop(FeaturedStatus.FEATURED,shopId,VERIFIED);
        if(products.isEmpty())return List.of();
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1)
                .toList();
        return filtered.stream()
                .map(ProductResponse::new)
                .toList();
    }

    @Override
    public String updatePrice(Long productId, Double price) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setSalesPrice(BigDecimal.valueOf(price));
        productRepo.save(product);
        return "Product successfully updated";
    }


    @Override
    public ApiResponse<Void> updateProductTotalStock(Long id, Integer quantity) {
        try {
            Product product = productRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            product.setQuantity(product.getQuantity() + quantity);
            productRepo.save(product);
            return new ApiResponse<>(true, "Quantity updated successfully", null);
        } catch (ResourceNotFoundException | BusinessException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    @Override
    public String promote(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if(product.isPromoted())
            throw new BusinessException("product already promoted");
        Shop shop = product.getShop();
        if(Objects.equals(shop.getPromotedNumber(), shop.getShopTier().getPromotedNumber()))
            throw new BusinessException("Exhausted promoted products, consider upgrading");
        product.setPromoted(true);
        shop.setPromotedNumber(shop.getPromotedNumber() + 1);
        shopRepo.save(shop);
        product.setPromotedAt(LocalDateTime.now());
        productRepo.save(product);
        return "product promoted";
    }

    @Override
    public String floatProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if(product.isFloated())
            throw new BusinessException("product already floated");
        Shop shop = product.getShop();
        if(Objects.equals(shop.getFloatedNumber(), shop.getShopTier().getFloatedNumber()))
            throw new BusinessException("Exhausted floated products, consider upgrading");
        product.setFloated(true);
        shop.setFloatedNumber(shop.getFloatedNumber() + 1);
        shopRepo.save(shop);
        product.setFloatedAt(LocalDateTime.now());
        productRepo.save(product);
        return "product floated";
    }

    @Override
    public List<ProductResponse> getAllFloatedProducts() {
        List<Product>products = productRepo.findAllByFloated(true);
        if(products.isEmpty())return List.of();
        List<Product>filtered = products.stream()
                .filter(product -> product.getQuantity()-product.getQuantitySold()>=1)
                .toList();
        return filtered.stream()
                .map(ProductResponse::new)
                .toList();
    }

    @Override
    public List<ProductResponse> getAllPromotedProducts() {
        List<Product>products = productRepo.findAllByPromoted(true);
        if(products.isEmpty())return List.of();
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1)
                .toList();
        return filtered.stream()
                .map(ProductResponse::new)
                .toList();
    }

    @Override
    public List<ProductResponse> getAllShopProducts(Long shopId) {
        List<Product>products = productRepo.findAllByShop(shopId);
        if(products.isEmpty())return List.of();
        return products.stream()
                .map(ProductResponse::new)
                .toList();
    }





    @Override
    public Long getAllProductsByCategoryCount(Long categoryId) {
        List<Product> products = productRepo.findByCategory(categoryId,VERIFIED,ACTIVE,ACTIVE);
        List<Product>filtered = products.stream()
                .filter(product->product.getQuantity()-product.getQuantitySold()>=1)
                .toList();
        return (long) filtered.size();
    }

    private ProductResponseDto mapToDto4SingleProduct(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());
        dto.setQuantitySold(product.getQuantitySold());
        // Set image URLs
        dto.setMainImageUrl(product.getMainImageUrl());
        dto.setSideImage1Url(product.getSideImage1Url());
        dto.setSideImage2Url(product.getSideImage2Url());
        dto.setSideImage3Url(product.getSideImage3Url());
        dto.setSideImage4Url(product.getSideImage4Url());

        // Set shop info
        dto.setShopId(product.getShop().getId());
        dto.setShopName(product.getShop().getName());

        return dto;

    }
}
