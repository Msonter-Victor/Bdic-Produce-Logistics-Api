/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.response.DisputeResponse
 *  dev.gagnon.dto.response.OrderItemDto
 *  dev.gagnon.model.Dispute
 *  lombok.Generated
 */
package dev.gagnon.dto.response;

import dev.gagnon.dto.response.OrderItemDto;
import dev.gagnon.model.Dispute;
import lombok.Generated;

public class DisputeResponse {
    private Long id;
    private String requestTime;
    private String status;
    private String resolvedDate;
    private String orderNumber;
    private String imageUrl;
    private String reason;
    private OrderItemDto orderItem;
    private String orderTime;
    private String deliveryMethod;

    public DisputeResponse(Dispute dispute) {
        this.id = dispute.getId();
        this.requestTime = String.valueOf(dispute.getRequestDate());
        this.status = String.valueOf(dispute.getStatus());
        this.resolvedDate = String.valueOf(dispute.getResolvedDate());
        this.orderNumber = dispute.getOrderNumber();
        this.orderItem = new OrderItemDto(dispute.getItem());
        this.imageUrl = dispute.getImageUrl();
        this.reason = dispute.getReason();
        this.orderTime = String.valueOf(dispute.getItem().getOrder().getCreatedAt());
        this.deliveryMethod = dispute.getItem().getOrder().getDeliveryInfo().getMethod();
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    @Generated
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated
    public void setResolvedDate(String resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    @Generated
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Generated
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Generated
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Generated
    public void setOrderItem(OrderItemDto orderItem) {
        this.orderItem = orderItem;
    }

    @Generated
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    @Generated
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getRequestTime() {
        return this.requestTime;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public String getResolvedDate() {
        return this.resolvedDate;
    }

    @Generated
    public String getOrderNumber() {
        return this.orderNumber;
    }

    @Generated
    public String getImageUrl() {
        return this.imageUrl;
    }

    @Generated
    public String getReason() {
        return this.reason;
    }

    @Generated
    public OrderItemDto getOrderItem() {
        return this.orderItem;
    }

    @Generated
    public String getOrderTime() {
        return this.orderTime;
    }

    @Generated
    public String getDeliveryMethod() {
        return this.deliveryMethod;
    }

    @Generated
    public DisputeResponse(Long id, String requestTime, String status, String resolvedDate, String orderNumber, String imageUrl, String reason, OrderItemDto orderItem, String orderTime, String deliveryMethod) {
        this.id = id;
        this.requestTime = requestTime;
        this.status = status;
        this.resolvedDate = resolvedDate;
        this.orderNumber = orderNumber;
        this.imageUrl = imageUrl;
        this.reason = reason;
        this.orderItem = orderItem;
        this.orderTime = orderTime;
        this.deliveryMethod = deliveryMethod;
    }

    @Generated
    public DisputeResponse() {
    }
}

