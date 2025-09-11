/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.CouponRequest
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import lombok.Generated;

public class CouponRequest {
    private Long shopId;
    private int percentageDiscount;

    @Generated
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Generated
    public void setPercentageDiscount(int percentageDiscount) {
        this.percentageDiscount = percentageDiscount;
    }

    @Generated
    public Long getShopId() {
        return this.shopId;
    }

    @Generated
    public int getPercentageDiscount() {
        return this.percentageDiscount;
    }

    @Generated
    public CouponRequest(Long shopId, int percentageDiscount) {
        this.shopId = shopId;
        this.percentageDiscount = percentageDiscount;
    }

    @Generated
    public CouponRequest() {
    }
}

