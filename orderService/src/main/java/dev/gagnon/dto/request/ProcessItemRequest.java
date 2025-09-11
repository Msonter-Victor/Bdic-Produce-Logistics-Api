package dev.gagnon.dto.request;

import lombok.Generated;

import java.util.List;

public class ProcessItemRequest {
    private String orderNumber;
    private List<Long> itemIds;

    @Generated
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Generated
    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    @Generated
    public String getOrderNumber() {
        return this.orderNumber;
    }

    @Generated
    public List<Long> getItemIds() {
        return this.itemIds;
    }

    @Generated
    public ProcessItemRequest(String orderNumber, List<Long> itemIds) {
        this.orderNumber = orderNumber;
        this.itemIds = itemIds;
    }

    @Generated
    public ProcessItemRequest() {
    }
}

