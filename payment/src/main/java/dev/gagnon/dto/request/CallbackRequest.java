package dev.gagnon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallbackRequest {
    private Integer status;
    private String errorMessage;
    private String transRef;
    private String transAmount;
    private String currency;
    private String reference;
}