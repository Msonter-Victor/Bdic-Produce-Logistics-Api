/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.response.ProductResponse
 *  lombok.Generated
 */
package dev.gagnon.dto.response;

import lombok.Generated;

import java.math.BigDecimal;

public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
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

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Generated
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Generated
    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    @Generated
    public void setSideImage1Url(String sideImage1Url) {
        this.sideImage1Url = sideImage1Url;
    }

    @Generated
    public void setSideImage2Url(String sideImage2Url) {
        this.sideImage2Url = sideImage2Url;
    }

    @Generated
    public void setSideImage3Url(String sideImage3Url) {
        this.sideImage3Url = sideImage3Url;
    }

    @Generated
    public void setSideImage4Url(String sideImage4Url) {
        this.sideImage4Url = sideImage4Url;
    }

    @Generated
    public void setCategory(String category) {
        this.category = category;
    }

    @Generated
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    @Generated
    public void setMarket(String market) {
        this.market = market;
    }

    @Generated
    public void setMarketSection(String marketSection) {
        this.marketSection = marketSection;
    }

    @Generated
    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    @Generated
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @Generated
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Generated
    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    @Generated
    public void setQuantitySold(Long quantitySold) {
        this.quantitySold = quantitySold;
    }

    @Generated
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @Generated
    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    @Generated
    public void setCity(String city) {
        this.city = city;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public BigDecimal getPrice() {
        return this.price;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public Integer getQuantity() {
        return this.quantity;
    }

    @Generated
    public String getMainImageUrl() {
        return this.mainImageUrl;
    }

    @Generated
    public String getSideImage1Url() {
        return this.sideImage1Url;
    }

    @Generated
    public String getSideImage2Url() {
        return this.sideImage2Url;
    }

    @Generated
    public String getSideImage3Url() {
        return this.sideImage3Url;
    }

    @Generated
    public String getSideImage4Url() {
        return this.sideImage4Url;
    }

    @Generated
    public String getCategory() {
        return this.category;
    }

    @Generated
    public String getSubCategory() {
        return this.subCategory;
    }

    @Generated
    public String getMarket() {
        return this.market;
    }

    @Generated
    public String getMarketSection() {
        return this.marketSection;
    }

    @Generated
    public String getShopNumber() {
        return this.shopNumber;
    }

    @Generated
    public String getShopName() {
        return this.shopName;
    }

    @Generated
    public Long getShopId() {
        return this.shopId;
    }

    @Generated
    public String getShopAddress() {
        return this.shopAddress;
    }

    @Generated
    public Long getQuantitySold() {
        return this.quantitySold;
    }

    @Generated
    public String getVendorName() {
        return this.vendorName;
    }

    @Generated
    public String getVendorEmail() {
        return this.vendorEmail;
    }

    @Generated
    public String getCity() {
        return this.city;
    }

    @Generated
    public ProductResponse(Long id, String name, BigDecimal price, String description, Integer quantity, String mainImageUrl, String sideImage1Url, String sideImage2Url, String sideImage3Url, String sideImage4Url, String category, String subCategory, String market, String marketSection, String shopNumber, String shopName, Long shopId, String shopAddress, Long quantitySold, String vendorName, String vendorEmail, String city) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
        this.mainImageUrl = mainImageUrl;
        this.sideImage1Url = sideImage1Url;
        this.sideImage2Url = sideImage2Url;
        this.sideImage3Url = sideImage3Url;
        this.sideImage4Url = sideImage4Url;
        this.category = category;
        this.subCategory = subCategory;
        this.market = market;
        this.marketSection = marketSection;
        this.shopNumber = shopNumber;
        this.shopName = shopName;
        this.shopId = shopId;
        this.shopAddress = shopAddress;
        this.quantitySold = quantitySold;
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
        this.city = city;
    }

    @Generated
    public ProductResponse() {
    }
}

