package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    private String deliveryOption;
    private String deliveryAddress;
    private String couponCode;
    
    // getters and setters
}