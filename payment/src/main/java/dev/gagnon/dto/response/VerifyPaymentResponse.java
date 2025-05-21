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
    private PaymentData data;
    private Double execTime;
    private Object[] error;
    private String errorMessage;

    @Data
    public static class PaymentData {
        private String businessCode;
        private String transRef;
        private String businessRef;
        private Double debitedAmount;
        private Double transAmount;
        private Double transFeeAmount;
        private Double settlementAmount;
        private String customerId;
        private String transactionDate;
        private Integer channelId;
        private String currencyCode;
        private Integer status;
        private List<Metadata> metadata;
    }

    @Data
    public static class Metadata {
        private String insightTag;
        private String insightTagValue;
        private String insightTagDisplay;
    }
}