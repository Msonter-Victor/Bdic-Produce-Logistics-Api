package dev.gagnon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String message;
    private String conversationState;
    private List<String> suggestions;
    private List<MarketResult> marketResults;
    private List<ShopResult> shopResults;
    private boolean requiresInput;
    private String redirectUrl;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MarketResult {
        private Long marketId;
        private String marketName;
        private String description;
        private String address;
        private int shopCount;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShopResult {
        private Long shopId;
        private String shopName;
        private String address;
        private String market;
        private String marketSection;
        private int productCount;
    }
}