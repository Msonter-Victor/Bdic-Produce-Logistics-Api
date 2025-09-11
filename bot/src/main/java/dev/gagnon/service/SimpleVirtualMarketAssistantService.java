package dev.gagnon.service;

import dev.gagnon.dto.ChatRequest;
import dev.gagnon.dto.ChatResponse;
import dev.gagnon.model.ChatSession;
import dev.gagnon.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleVirtualMarketAssistantService {
    
    private final ChatSessionRepository chatSessionRepository;
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.vendor-service:http://localhost:8083}")
    private String vendorServiceUrl;

    public ChatResponse processMessage(ChatRequest request) {
        try {
            ChatSession session = getOrCreateSession(request.getSessionId());
            
            switch (session.getConversationState()) {
                case "INITIAL":
                    return handleInitialMessage(request, session);
                case "PRODUCT_SEARCH":
                    return handleProductSearch(request, session);
                case "MARKET_SELECTION":
                    return handleMarketSelection(request, session);
                default:
                    return resetConversation(session);
            }
        } catch (Exception e) {
            log.error("Error processing message: ", e);
            return new ChatResponse(
                "I'm sorry, I encountered an error. Let's start over. What are you looking to buy today?",
                "INITIAL",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                true,
                null
            );
        }
    }
    
    private ChatResponse handleInitialMessage(ChatRequest request, ChatSession session) {
        String welcomeMessage = "Hello! Welcome to BDIC Virtual Market. I'm here to help you find what you need. What are you looking to buy today?";
        
        session.setConversationState("PRODUCT_SEARCH");
        chatSessionRepository.save(session);
        
        return new ChatResponse(
            welcomeMessage,
            "PRODUCT_SEARCH",
            Arrays.asList("Fruits", "Vegetables", "Electronics", "Clothing", "Food items"),
            Collections.emptyList(),
            Collections.emptyList(),
            true,
            null
        );
    }
    
    private ChatResponse handleProductSearch(ChatRequest request, ChatSession session) {
        String userQuery = request.getMessage().toLowerCase().trim();
        session.setSearchQuery(userQuery);
        
        // Search for products using the vendor service
        List<Map<String, Object>> products = searchProducts(userQuery);
        
        if (products.isEmpty()) {
            return new ChatResponse(
                "I couldn't find any products matching '" + userQuery + "'. Could you try a different search term or be more specific?",
                "PRODUCT_SEARCH",
                Arrays.asList("Try different keywords", "Be more specific", "Check spelling"),
                Collections.emptyList(),
                Collections.emptyList(),
                true,
                null
            );
        }
        
        // Get unique markets from the search results
        Set<String> availableMarkets = products.stream()
            .map(product -> (String) product.get("market"))
            .filter(Objects::nonNull)
            .filter(market -> !market.isEmpty())
            .collect(Collectors.toSet());
        
        if (availableMarkets.isEmpty()) {
            availableMarkets.add("Wurukum Market");
            availableMarkets.add("Modern Market");
            availableMarkets.add("Gboko Market");
        }
        
        session.setConversationState("MARKET_SELECTION");
        session.setContextData(String.join(",", availableMarkets));
        chatSessionRepository.save(session);
        
        String responseMessage = String.format(
            "Great! I found %d products matching '%s'. Which market would you like to shop from?",
            products.size(), userQuery
        );
        
        return new ChatResponse(
            responseMessage,
            "MARKET_SELECTION",
            new ArrayList<>(availableMarkets),
            Collections.emptyList(),
            Collections.emptyList(),
            true,
            null
        );
    }
    
    private ChatResponse handleMarketSelection(ChatRequest request, ChatSession session) {
        String selectedMarket = request.getMessage().trim();
        session.setSelectedMarket(selectedMarket);
        
        // Search for shops in the selected market that have the searched product
        List<ChatResponse.ShopResult> shops = findShopsWithProduct(session.getSearchQuery(), selectedMarket);
        
        if (shops.isEmpty()) {
            return new ChatResponse(
                "I couldn't find any shops in " + selectedMarket + " that have '" + session.getSearchQuery() + "'. Would you like to try a different market?",
                "MARKET_SELECTION",
                Arrays.asList(session.getContextData().split(",")),
                Collections.emptyList(),
                Collections.emptyList(),
                true,
                null
            );
        }
        
        session.setConversationState("SHOP_RESULTS");
        chatSessionRepository.save(session);
        
        String responseMessage = String.format(
            "Perfect! I found %d shops in %s that have '%s'. Click on any shop below to visit them:",
            shops.size(), selectedMarket, session.getSearchQuery()
        );
        
        return new ChatResponse(
            responseMessage,
            "SHOP_RESULTS",
            Collections.emptyList(),
            Collections.emptyList(),
            shops,
            false,
            null
        );
    }
    
    private List<Map<String, Object>> searchProducts(String query) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(vendorServiceUrl).build();
            
            List<Map<String, Object>> results = webClient.get()
                .uri("/products/search?query=" + query)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
                
            return results != null ? results : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error searching products: ", e);
            return Collections.emptyList();
        }
    }
    
    private List<ChatResponse.ShopResult> findShopsWithProduct(String productQuery, String market) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(vendorServiceUrl).build();
            
            List<Map<String, Object>> results = webClient.get()
                .uri("/shops/search?product=" + productQuery + "&market=" + market)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
                
            if (results != null) {
                return results.stream().map(shop -> {
                    ChatResponse.ShopResult shopResult = new ChatResponse.ShopResult();
                    shopResult.setShopId(((Number) shop.get("shopId")).longValue());
                    shopResult.setShopName((String) shop.get("shopName"));
                    shopResult.setAddress((String) shop.get("address"));
                    shopResult.setMarket((String) shop.get("market"));
                    shopResult.setMarketSection((String) shop.get("marketSection"));
                    shopResult.setProductCount(((Number) shop.get("productCount")).intValue());
                    return shopResult;
                }).collect(Collectors.toList());
            }
            
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error finding shops: ", e);
            return Collections.emptyList();
        }
    }
    
    private ChatSession getOrCreateSession(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
            .orElseGet(() -> {
                ChatSession newSession = new ChatSession();
                newSession.setSessionId(sessionId);
                newSession.setConversationState("INITIAL");
                return chatSessionRepository.save(newSession);
            });
    }
    
    private ChatResponse resetConversation(ChatSession session) {
        session.setConversationState("INITIAL");
        session.setSearchQuery(null);
        session.setSelectedMarket(null);
        session.setContextData(null);
        chatSessionRepository.save(session);
        
        return handleInitialMessage(new ChatRequest("", session.getSessionId(), "INITIAL"), session);
    }
    
    public void clearSession(String sessionId) {
        chatSessionRepository.deleteBySessionId(sessionId);
    }
}