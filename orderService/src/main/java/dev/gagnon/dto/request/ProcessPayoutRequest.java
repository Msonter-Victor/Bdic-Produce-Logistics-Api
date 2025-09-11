/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.ProcessPayoutRequest
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import lombok.Generated;

public class ProcessPayoutRequest {
    private Long shopId;
    private double amount;

    @Generated
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Generated
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Generated
    public Long getShopId() {
        return this.shopId;
    }

    @Generated
    public double getAmount() {
        return this.amount;
    }

    @Generated
    public ProcessPayoutRequest(Long shopId, double amount) {
        this.shopId = shopId;
        this.amount = amount;
    }

    @Generated
    public ProcessPayoutRequest() {
    }
}

