/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.CartItemDto
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import lombok.Generated;

import java.math.BigDecimal;

public class CartItemDto {
    private Long productId;
    private Long itemId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;

    @Generated
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Generated
    public void setItemId(Long itemId) {
        this.itemId = itemId;
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
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Generated
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Generated
    public Long getProductId() {
        return this.productId;
    }

    @Generated
    public Long getItemId() {
        return this.itemId;
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
    public int getQuantity() {
        return this.quantity;
    }

    @Generated
    public String getImageUrl() {
        return this.imageUrl;
    }

    @Generated
    public CartItemDto(Long productId, Long itemId, String name, BigDecimal price, int quantity, String imageUrl) {
        this.productId = productId;
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    @Generated
    public CartItemDto() {
    }
}

