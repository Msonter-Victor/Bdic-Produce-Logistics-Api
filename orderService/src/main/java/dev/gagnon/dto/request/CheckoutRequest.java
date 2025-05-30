package dev.gagnon.dto.request;

import dev.gagnon.model.DeliveryMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private DeliveryMethod deliveryMethod;
    
    private String address;

    
    // For shop pickup
    private String shopLocation;
    private LocalDateTime pickupTime;
}