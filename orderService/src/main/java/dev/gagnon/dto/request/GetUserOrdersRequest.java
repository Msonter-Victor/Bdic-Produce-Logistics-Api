/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.GetUserOrdersRequest
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import lombok.Generated;

public class GetUserOrdersRequest {
    private String buyerEmail;

    @Generated
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    @Generated
    public String getBuyerEmail() {
        return this.buyerEmail;
    }

    @Generated
    public GetUserOrdersRequest(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    @Generated
    public GetUserOrdersRequest() {
    }
}

