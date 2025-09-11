// VerifyPaymentResponse.java
package dev.gagnon.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyPaymentResponse {
    private String status;
    private String message;
    private Data data;

    @lombok.Data
    public static class Data {
        private String reference;
        private String status;
    }

}