package dev.gagnon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderRequest {
    private String orderNumber;
    private String customerName;
    private String method;
    private String address;
    private String contactPhone;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
}
