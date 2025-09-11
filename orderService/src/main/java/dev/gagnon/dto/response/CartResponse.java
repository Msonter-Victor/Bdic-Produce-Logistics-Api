/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.CartItemDto
 *  dev.gagnon.dto.response.CartResponse
 *  lombok.Generated
 */
package dev.gagnon.dto.response;

import dev.gagnon.dto.request.CartItemDto;
import lombok.Generated;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private String cartId;
    private List<CartItemDto> items;
    private BigDecimal totalAmount;
    private int totalItems;

    @Generated
    public String getCartId() {
        return this.cartId;
    }

    @Generated
    public List<CartItemDto> getItems() {
        return this.items;
    }

    @Generated
    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    @Generated
    public int getTotalItems() {
        return this.totalItems;
    }

    @Generated
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    @Generated
    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    @Generated
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Generated
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CartResponse)) {
            return false;
        }
        CartResponse other = (CartResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getTotalItems() != other.getTotalItems()) {
            return false;
        }
        String this$cartId = this.getCartId();
        String other$cartId = other.getCartId();
        if (this$cartId == null ? other$cartId != null : !this$cartId.equals(other$cartId)) {
            return false;
        }
        List this$items = this.getItems();
        List other$items = other.getItems();
        if (this$items == null ? other$items != null : !((Object)this$items).equals(other$items)) {
            return false;
        }
        BigDecimal this$totalAmount = this.getTotalAmount();
        BigDecimal other$totalAmount = other.getTotalAmount();
        return !(this$totalAmount == null ? other$totalAmount != null : !((Object)this$totalAmount).equals(other$totalAmount));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof CartResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTotalItems();
        String $cartId = this.getCartId();
        result = result * 59 + ($cartId == null ? 43 : $cartId.hashCode());
        List $items = this.getItems();
        result = result * 59 + ($items == null ? 43 : ((Object)$items).hashCode());
        BigDecimal $totalAmount = this.getTotalAmount();
        result = result * 59 + ($totalAmount == null ? 43 : ((Object)$totalAmount).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "CartResponse(cartId=" + this.getCartId() + ", items=" + String.valueOf(this.getItems()) + ", totalAmount=" + String.valueOf(this.getTotalAmount()) + ", totalItems=" + this.getTotalItems() + ")";
    }

    @Generated
    public CartResponse() {
    }

    @Generated
    public CartResponse(String cartId, List<CartItemDto> items, BigDecimal totalAmount, int totalItems) {
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
    }
}

