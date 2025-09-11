/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.PayoutRequest
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import lombok.Generated;

public class PayoutRequest {
    private Long shopId;
    private double amount;
    private String bankName;
    private String accountNumber;

    @Generated
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Generated
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Generated
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Generated
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
    public String getBankName() {
        return this.bankName;
    }

    @Generated
    public String getAccountNumber() {
        return this.accountNumber;
    }

    @Generated
    public PayoutRequest(Long shopId, double amount, String bankName, String accountNumber) {
        this.shopId = shopId;
        this.amount = amount;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    @Generated
    public PayoutRequest() {
    }
}

