//PaymentTransactionResponse
package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionResponse {
    private Long id;
    private String reference;
    private String email;
    private Integer amount;
    private String status;
    private String paymentType;
    private LocalDateTime createdAt;
}
