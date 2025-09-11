package dev.gagnon.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryOrderResponse {
    private Long id;
    private String orderNumber;
    private String customerName;
    private String contactPhone;
    private String deliveryAddress;
    private String deliveryMethod;
    private Double deliveryFee;
    private Double grandTotal;
    private String riderName;
    private String riderPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
