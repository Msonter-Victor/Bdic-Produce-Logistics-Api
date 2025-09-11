package dev.gagnon.service;

import dev.gagnon.dto.ChatRequest;
import dev.gagnon.dto.ChatResponse;
import dev.gagnon.model.ChatSession;
import dev.gagnon.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedVirtualMarketAssistantService {
    
    private final ChatSessionRepository chatSessionRepository;
    private final ServiceClient serviceClient;
    private final OpenAIService openAIService;
    private final SemanticProductSearchService semanticSearchService;
    
    private static final String SYSTEM_PROMPT = """
        You are BDIC Market Assistant, a helpful virtual marketplace assistant for BDIC Virtual Market.
        Your ONLY purpose is to help users find products and navigate to markets and shops.
        
        STRICT RULES:
        1. ONLY discuss marketplace-related topics: finding products, markets, and shops
        2. If user asks about anything else, respond: "Agent not permitted to answer that"
        3. Always be friendly and professional
        4. Your main greeting should be: "Hello, my name is BDIC Market Assistant, what are you buying today?"
        
        Current conversation flow:
        - INITIAL: Greet with standard message
        - PRODUCT_SEARCH: Help them find specific products and show available markets
        - MARKET_SELECTION: User selects a market, then redirect to market page
        - MARKET_REDIRECT: Confirm redirection to selected market
        """;

    public dev.gagnon.dto.ChatResponse processMessage(ChatRequest request) {
        try {
            ChatSession session = getOrCreateSession(request.getSessionId());
            String userName = getUserName(session.getSessionId());
            
            // Check if message is off-topic
            if (isOffTopicMessage(request.getMessage())) {
                return createOffTopicResponse();
            }
            
            switch (session.getConversationState()) {
                case "INITIAL":
                    return handleInitialMessage(request, session, userName);
                case "PRODUCT_SEARCH":
                    return handleProductSearch(request, session, userName);
                case "MARKET_SELECTION":
                    return handleMarketSelection(request, session, userName);
                default:
                    return resetConversation(session, userName);
            }
        } catch (Exception e) {
            log.error("Error processing message: ", e);
            return createErrorResponse();
        }
    }
    
    private boolean isOffTopicMessage(String message) {
        String lowerMessage = message.toLowerCase().trim();
        
        // List of marketplace-related keywords (products and shopping terms)
        Set<String> marketplaceKeywords = Set.of(
            "buy", "buying", "product", "shop", "store", "market", "price", "cost",
            "fruit", "vegetable", "food", "electronics", "clothing", "item",
            "purchase", "order", "vendor", "seller", "goods", "merchandise",
            "tomato", "onion", "rice", "beans", "pepper", "yam", "plantain", "garri",
            "phone", "laptop", "shirt", "available", "find", "search", "looking"
        );
        
        // List of off-topic indicators - anything not related to product search
        Set<String> offTopicKeywords = Set.of(
            "weather", "politics", "news", "sports", "movie", "music", "health",
            "relationship", "advice", "joke", "story", "personal", "family",
            "school", "work", "job", "travel", "religion", "philosophy", "love",
            "dating", "money", "loan", "investment", "bitcoin", "crypto", "stock",
            "government", "president", "election", "war", "peace", "history"
        );
        
        // Simple greetings and basic interactions are allowed
        Set<String> allowedGreetings = Set.of("hello", "hi", "hey", "good morning", 
            "good afternoon", "good evening", "thanks", "thank you", "yes", "no",
            "okay", "ok", "sure", "please", "help", "assist", "what", "how");
        
        boolean isGreeting = allowedGreetings.stream()
            .anyMatch(lowerMessage::contains);
            
        boolean hasMarketplaceKeywords = marketplaceKeywords.stream()
            .anyMatch(lowerMessage::contains);
            
        boolean hasOffTopicKeywords = offTopicKeywords.stream()
            .anyMatch(lowerMessage::contains);
        
        // If it's clearly off-topic and not marketplace related, block it
        if (hasOffTopicKeywords && !hasMarketplaceKeywords) {
            return true;
        }
        
        // If it's not a greeting and doesn't contain marketplace keywords, it might be off-topic
        // But we'll be lenient for short messages that could be product names
        if (!isGreeting && !hasMarketplaceKeywords && lowerMessage.length() > 20) {
            return true;
        }
        
        return false;
    }
    
    private dev.gagnon.dto.ChatResponse createOffTopicResponse() {
        return new dev.gagnon.dto.ChatResponse(
            "Agent not permitted to answer that",
            "PRODUCT_SEARCH",
            Arrays.asList("Fruits", "Vegetables", "Electronics", "Clothing", "Food items"),
            Collections.emptyList(),
            Collections.emptyList(),
            true,
            null
        );
    }
    
    private dev.gagnon.dto.ChatResponse handleInitialMessage(ChatRequest request, ChatSession session, String userName) {
        String welcomeMessage = "Hello, my name is BDIC Market Assistant, what are you buying today?";
        
        session.setConversationState("PRODUCT_SEARCH");
        chatSessionRepository.save(session);
        
        return new dev.gagnon.dto.ChatResponse(
            welcomeMessage,
            "PRODUCT_SEARCH",
            Arrays.asList("Tomatoes", "Rice", "Beans", "Onions", "Pepper", "Yam", "Plantain", "Garri"),
            Collections.emptyList(),
            Collections.emptyList(),
            true,
            null
        );
    }
    
    private dev.gagnon.dto.ChatResponse handleProductSearch(ChatRequest request, ChatSession session, String userName) {
        String userQuery = request.getMessage().toLowerCase().trim();
        session.setSearchQuery(userQuery);
        
        // Search for products and find markets that have them
        List<dev.gagnon.dto.ProductResponseDto> products = serviceClient.searchProducts(userQuery);
        
        if (!products.isEmpty()) {
            // Get markets that have these products
            List<dev.gagnon.dto.MarketResponseDto> marketsWithProducts = serviceClient.getMarketsWithProducts(userQuery);
            
            if (!marketsWithProducts.isEmpty()) {
                session.setConversationState("MARKET_SELECTION");
                chatSessionRepository.save(session);
                
                // Convert to MarketResult for response
                List<dev.gagnon.dto.ChatResponse.MarketResult> marketResults = marketsWithProducts.stream()
                    .map(market -> new dev.gagnon.dto.ChatResponse.MarketResult(
                        market.getId(),
                        market.getName(),
                        market.getDescription(),
                        market.getAddress(),
                        market.getShops() != null ? market.getShops().size() : 0
                    ))
                    .toList();
                
                return new dev.gagnon.dto.ChatResponse(
                    String.format("Found %d products matching '%s'. Which market would you like to visit?", 
                                products.size(), userQuery),
                    "MARKET_SELECTION",
                    Collections.emptyList(),
                    marketResults,
                    Collections.emptyList(),
                    true,
                    null
                );
            }
        }
        
        // No products found, provide suggestions
        List<String> productSuggestions = getProductSuggestions(userQuery);
        
        return new dev.gagnon.dto.ChatResponse(
            "I couldn't find that product. Here are some suggestions:",
            "PRODUCT_SEARCH",
            productSuggestions.isEmpty() ? 
                Arrays.asList("Tomatoes", "Rice", "Beans", "Onions", "Pepper", "Yam", "Plantain", "Garri") :
                productSuggestions,
            Collections.emptyList(),
            Collections.emptyList(),
            true,
            null
        );
    }
    
    private List<String> getProductSuggestions(String query) {
        try {
            // Search for products with partial match
            List<dev.gagnon.dto.ProductResponseDto> products = serviceClient.searchProducts(query);
            
            return products.stream()
                .map(dev.gagnon.dto.ProductResponseDto::getName)
                .filter(Objects::nonNull)
                .distinct()
                .limit(8)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting product suggestions: ", e);
            return Arrays.asList("Tomatoes", "Rice", "Beans", "Onions", "Pepper", "Yam");
        }
    }
    
    private boolean checkProductExists(String productName) {
        try {
            List<dev.gagnon.dto.ProductResponseDto> products = serviceClient.searchProducts(productName);
            return products.stream()
                .anyMatch(product -> product.getName() != null && 
                         product.getName().toLowerCase().equals(productName.toLowerCase()));
        } catch (Exception e) {
            log.error("Error checking product existence: ", e);
            return false;
        }
    }
    
    private dev.gagnon.dto.ChatResponse handleMarketSelection(ChatRequest request, ChatSession session, String userName) {
        String userMessage = request.getMessage().toLowerCase().trim();
        
        // Try to parse market selection (could be market name or ID)
        try {
            // Check if user entered a market name or selected one
            List<dev.gagnon.dto.MarketResponseDto> allMarkets = serviceClient.getAllMarkets();
            
            dev.gagnon.dto.MarketResponseDto selectedMarket = null;
            
            // Try to find market by name
            for (dev.gagnon.dto.MarketResponseDto market : allMarkets) {
                if (market.getName().toLowerCase().contains(userMessage) || 
                    userMessage.contains(market.getName().toLowerCase())) {
                    selectedMarket = market;
                    break;
                }
            }
            
            // Try to parse as market ID
            if (selectedMarket == null) {
                try {
                    Long marketId = Long.parseLong(userMessage);
                    selectedMarket = allMarkets.stream()
                        .filter(market -> market.getId().equals(marketId))
                        .findFirst()
                        .orElse(null);
                } catch (NumberFormatException e) {
                    // Not a number, continue
                }
            }
            
            if (selectedMarket != null) {
                session.setSelectedMarket(selectedMarket.getName());
                session.setConversationState("INITIAL"); // Reset for next interaction
                chatSessionRepository.save(session);
                
                String redirectUrl = "/marketPlace/view-market/" + selectedMarket.getId();
                
                return new dev.gagnon.dto.ChatResponse(
                    String.format("Redirecting you to %s market...", selectedMarket.getName()),
                    "MARKET_REDIRECT",
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    false,
                    redirectUrl
                );
            }
            
        } catch (Exception e) {
            log.error("Error handling market selection: ", e);
        }
        
        // If no market found, ask for clarification
        return new dev.gagnon.dto.ChatResponse(
            "I couldn't find that market. Please select from the available markets or search for a different product.",
            "PRODUCT_SEARCH",
            Arrays.asList("Search for another product", "View all markets"),
            Collections.emptyList(),
            Collections.emptyList(),
            true,
            null
        );
    }
    

    

    
    private String getUserName(String sessionId) {
        try {
            Map<String, Object> userInfo = serviceClient.getUserInfo(sessionId);
            if (!userInfo.isEmpty()) {
                String firstName = (String) userInfo.get("firstName");
                String lastName = (String) userInfo.get("lastName");
                if (firstName != null) {
                    return lastName != null ? firstName + " " + lastName : firstName;
                }
            }
            return null; // No user name available
        } catch (Exception e) {
            log.error("Error getting user name: ", e);
            return null;
        }
    }
    
    @org.springframework.transaction.annotation.Transactional
    private ChatSession getOrCreateSession(String sessionId) {
        // First try to find existing session
        Optional<ChatSession> existingSession = chatSessionRepository.findBySessionId(sessionId);
        if (existingSession.isPresent()) {
            return existingSession.get();
        }
        
        // If not found, try to create new session with proper error handling
        try {
            ChatSession newSession = new ChatSession();
            newSession.setSessionId(sessionId);
            newSession.setConversationState("INITIAL");
            return chatSessionRepository.save(newSession);
        } catch (Exception e) {
            // If save fails due to duplicate key, try to find the session again
            // This handles the race condition where another thread created it
            log.warn("Failed to create session {}, attempting to find existing: {}", sessionId, e.getMessage());
            return chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Unable to create or find session: " + sessionId));
        }
    }
    
    private dev.gagnon.dto.ChatResponse resetConversation(ChatSession session, String userName) {
        session.setConversationState("INITIAL");
        session.setSearchQuery(null);
        session.setSelectedMarket(null);
        session.setContextData(null);
        chatSessionRepository.save(session);
        
        return handleInitialMessage(new ChatRequest("Hello", session.getSessionId(), "INITIAL"), session, userName);
    }
    
    private dev.gagnon.dto.ChatResponse createErrorResponse() {
        return new dev.gagnon.dto.ChatResponse(
            "I'm sorry, I encountered an error. Let's start over. What are you looking to buy today?",
            "INITIAL",
            Arrays.asList("Fruits", "Vegetables", "Electronics", "Clothing"),
            Collections.emptyList(),
            Collections.emptyList(),
            true,
            null
        );
    }
    
    public void clearSession(String sessionId) {
        chatSessionRepository.deleteBySessionId(sessionId);
    }
}