package dev.gagnon.DTO.response;

import dev.gagnon.Model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal salesPrice;
    private String description;
    private Integer quantity;
    private String mainImageUrl;
    private String sideImage1Url;
    private String sideImage2Url;
    private String sideImage3Url;
    private String sideImage4Url;
    private String category;
    private String subCategory;
    private String market;
    private String marketSection;
    private String shopNumber;
    private String shopName;
    private Long shopId;
    private String shopAddress;
    private Long quantitySold;
    private String vendorName;
    private String vendorEmail;
    private String city;
    List<ReviewResponse> reviews;
    public ProductResponse(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.salesPrice = product.getSalesPrice();
        this.quantity = Math.toIntExact(product.getQuantity() - product.getQuantitySold());
        this.shopId = product.getShop().getId();
        this.description = product.getDescription();
        this.mainImageUrl= product.getMainImageUrl();
        this.sideImage1Url= product.getSideImage1Url();
        this.sideImage2Url= product.getSideImage2Url();
        this.quantitySold= product.getQuantitySold();
        this.sideImage3Url= product.getSideImage3Url();
        this.sideImage4Url= product.getSideImage4Url();
        this.category = product.getCategory().getName();
        this.subCategory = product.getSubCategory().getName();
        this.shopName = product.getShop().getName();
        this.shopNumber = product.getShop().getShopNumber();
        this.shopAddress = product.getShop().getAddress();
        this.market = product.getShop().getMarket().getName();
        this.marketSection = product.getShop().getMarketSection().getName();
        this.vendorName = product.getShop().getUser().getFirstName();
        this.city = product.getShop().getMarket().getCity();
        this.vendorEmail = product.getShop().getUser().getEmail();
    }
}
