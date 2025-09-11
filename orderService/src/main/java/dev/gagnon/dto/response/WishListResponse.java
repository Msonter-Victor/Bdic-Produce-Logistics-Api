/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.response.WishListResponse
 *  dev.gagnon.model.WishListItem
 *  lombok.Generated
 */
package dev.gagnon.dto.response;

import dev.gagnon.model.WishListItem;
import lombok.Generated;

import java.util.List;

public class WishListResponse {
    private Long id;
    private String buyerEmail;
    private List<WishListItem> wishList;

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    @Generated
    public void setWishList(List<WishListItem> wishList) {
        this.wishList = wishList;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getBuyerEmail() {
        return this.buyerEmail;
    }

    @Generated
    public List<WishListItem> getWishList() {
        return this.wishList;
    }

    @Generated
    public WishListResponse(Long id, String buyerEmail, List<WishListItem> wishList) {
        this.id = id;
        this.buyerEmail = buyerEmail;
        this.wishList = wishList;
    }

    @Generated
    public WishListResponse() {
    }
}

