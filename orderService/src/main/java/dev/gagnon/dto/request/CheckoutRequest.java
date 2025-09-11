
package dev.gagnon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    private String buyerEmail;
    private String deliveryMethod;
    private double deliveryFee;
    private String address;
    private String reference;
    private String phoneNumber;
}

