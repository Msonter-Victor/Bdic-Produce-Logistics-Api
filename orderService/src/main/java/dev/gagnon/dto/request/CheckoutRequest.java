package dev.gagnon.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    @NotBlank
    private String deliveryMethod; // "pickup" or "delivery"
    
    @NotBlank
    private String address;
    
    private String paymentMethod;
}