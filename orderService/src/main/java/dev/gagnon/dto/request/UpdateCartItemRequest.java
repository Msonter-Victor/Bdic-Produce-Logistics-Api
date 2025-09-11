/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.UpdateCartItemRequest
 *  jakarta.validation.constraints.Min
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Generated;

public class UpdateCartItemRequest {
    @Min(value=1L)
    private @Min(value=1L) int quantity;

    @Generated
    public int getQuantity() {
        return this.quantity;
    }

    @Generated
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UpdateCartItemRequest)) {
            return false;
        }
        UpdateCartItemRequest other = (UpdateCartItemRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        return this.getQuantity() == other.getQuantity();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UpdateCartItemRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getQuantity();
        return result;
    }

    @Generated
    public String toString() {
        return "UpdateCartItemRequest(quantity=" + this.getQuantity() + ")";
    }

    @Generated
    public UpdateCartItemRequest() {
    }

    @Generated
    public UpdateCartItemRequest(int quantity) {
        this.quantity = quantity;
    }
}

