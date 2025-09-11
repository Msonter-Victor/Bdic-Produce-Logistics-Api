package dev.gagnon.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private String status;
    private String message;
    private Data data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data{
        private double amount;
        private String reference;
        private String recipient;
    }
}
