package dev.gagnon.dto.request;

import lombok.Generated;

public class ProcessRequest {
    private String orderNumber;

    @Generated
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Generated
    public String getOrderNumber() {
        return this.orderNumber;
    }

    @Generated
    public ProcessRequest(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Generated
    public ProcessRequest() {
    }
}

