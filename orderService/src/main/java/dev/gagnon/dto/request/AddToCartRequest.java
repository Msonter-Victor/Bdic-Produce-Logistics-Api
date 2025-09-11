/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.AddToCartRequest
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotNull
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;

public class AddToCartRequest {
    @NotNull
    private Long productId;
    @Min(value=1L)
    private @Min(value=1L) int quantity;

    @Generated
    public Long getProductId() {
        return this.productId;
    }

    @Generated
    public int getQuantity() {
        return this.quantity;
    }

    @Generated
    public void setProductId(Long productId) {
        this.productId = productId;
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
        if (!(o instanceof AddToCartRequest)) {
            return false;
        }
        AddToCartRequest other = (AddToCartRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        Long this$productId = this.getProductId();
        Long other$productId = other.getProductId();
        return !(this$productId == null ? other$productId != null : !((Object)this$productId).equals(other$productId));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AddToCartRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getQuantity();
        Long $productId = this.getProductId();
        result = result * 59 + ($productId == null ? 43 : ((Object)$productId).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AddToCartRequest(productId=" + this.getProductId() + ", quantity=" + this.getQuantity() + ")";
    }

    @Generated
    public AddToCartRequest() {
    }

    @Generated
    public AddToCartRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}

