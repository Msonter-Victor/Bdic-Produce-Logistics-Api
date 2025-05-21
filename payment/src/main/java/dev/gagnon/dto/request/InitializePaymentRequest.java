// InitializePaymentRequest.java
package dev.gagnon.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitializePaymentRequest {
    private String email;
    
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Integer amount;
    private String currency;
    private String callbackUrl;
}