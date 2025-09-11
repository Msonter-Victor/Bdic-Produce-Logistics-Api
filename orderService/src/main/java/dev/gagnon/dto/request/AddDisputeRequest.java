/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.AddDisputeRequest
 *  lombok.Generated
 *  org.springframework.web.multipart.MultipartFile
 */
package dev.gagnon.dto.request;

import lombok.Generated;
import org.springframework.web.multipart.MultipartFile;

public class AddDisputeRequest {
    private String buyerEmail;
    private String orderNumber;
    private Long itemId;
    private MultipartFile itemImage;
    private String reason;

    @Generated
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    @Generated
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Generated
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Generated
    public void setItemImage(MultipartFile itemImage) {
        this.itemImage = itemImage;
    }

    @Generated
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Generated
    public String getBuyerEmail() {
        return this.buyerEmail;
    }

    @Generated
    public String getOrderNumber() {
        return this.orderNumber;
    }

    @Generated
    public Long getItemId() {
        return this.itemId;
    }

    @Generated
    public MultipartFile getItemImage() {
        return this.itemImage;
    }

    @Generated
    public String getReason() {
        return this.reason;
    }

    @Generated
    public AddDisputeRequest(String buyerEmail, String orderNumber, Long itemId, MultipartFile itemImage, String reason) {
        this.buyerEmail = buyerEmail;
        this.orderNumber = orderNumber;
        this.itemId = itemId;
        this.itemImage = itemImage;
        this.reason = reason;
    }

    @Generated
    public AddDisputeRequest() {
    }
}

