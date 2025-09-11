package dev.gagnon.dto.response;

import dev.gagnon.model.Refund;
import dev.gagnon.model.constants.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String buyerEmail;
    private String buyerName;
    private String vendorEmail;
    private String vendorName;
    private String shopName;
    private BigDecimal refundAmount;
    private String reason;
    private String description;
    private RefundStatus status;
    private String processedBy;
    private String refundReference;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    
    public RefundResponse(Refund refund){
        this.id = refund.getId();
        this.orderId = refund.getOrderId();
        this.orderNumber = refund.getOrderNumber() != null ? refund.getOrderNumber() : "ORD-" + refund.getOrderId();
        this.buyerName = refund.getBuyerName();
        this.vendorName = refund.getVendorName();
        this.refundAmount = refund.getRefundAmount();
        this.reason = refund.getReason();
        this.status = refund.getStatus();
        this.adminNotes = refund.getAdminNotes();
        this.createdAt = refund.getCreatedAt();
        this.processedAt = refund.getProcessedAt();
        // Set default values for missing fields
        this.buyerEmail = "";
        this.vendorEmail = "";
        this.shopName = "Shop-" + refund.getShopId();
        this.description = refund.getReason();
        this.processedBy = "Admin";
        this.refundReference = "REF-" + refund.getId();
    }
}
