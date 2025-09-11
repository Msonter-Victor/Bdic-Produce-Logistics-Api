package dev.gagnon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgvector.PGvector;
import dev.gagnon.model.ConversationMemory;
import dev.gagnon.repository.ConversationMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorMemoryService {
    
    private final ConversationMemoryRepository memoryRepository;
    private final EmbeddingService embeddingService;
    private final ObjectMapper objectMapper;
    
    public void storeMemory(String sessionId, String content, String messageType, Map<String, Object> metadata) {
        try {
            PGvector embedding = embeddingService.generateEmbedding(content);
            String metadataJson = metadata != null ? objectMapper.writeValueAsString(metadata) : null;
            
            ConversationMemory memory = new ConversationMemory();
            memory.setSessionId(sessionId);
            memory.setContent(content);
            memory.setMessageType(messageType);
            memory.setEmbedding(embedding);
            memory.setMetadata(metadataJson);
            
            memoryRepository.save(memory);
            log.debug("Stored memory for session {}: {}", sessionId, content);
        } catch (Exception e) {
            log.error("Error storing memory for session {}: {}", sessionId, content, e);
        }
    }
    
    public List<ConversationMemory> getRecentMemories(String sessionId, int limit) {
        LocalDateTime since = LocalDateTime.now().minusHours(24); // Last 24 hours
        return memoryRepository.findRecentMemories(sessionId, since, limit);
    }
    
    public List<ConversationMemory> getSimilarMemories(String sessionId, String query, int limit) {
        try {
            PGvector queryEmbedding = embeddingService.generateEmbedding(query);
            String embeddingString = embeddingService.embeddingToString(queryEmbedding);
            return memoryRepository.findSimilarMemories(sessionId, embeddingString, limit);
        } catch (Exception e) {
            log.error("Error finding similar memories for session {}: {}", sessionId, query, e);
            return List.of();
        }
    }
    
    public String buildContextFromMemories(List<ConversationMemory> memories) {
        if (memories.isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder("Previous conversation context:\n");
        for (ConversationMemory memory : memories) {
            context.append(String.format("[%s] %s: %s\n", 
                memory.getTimestamp().toString(), 
                memory.getMessageType(), 
                memory.getContent()));
        }
        
        return context.toString();
    }
    
    public void clearSessionMemory(String sessionId) {
        memoryRepository.deleteBySessionId(sessionId);
        log.info("Cleared memory for session: {}", sessionId);
    }
    
    public Map<String, Object> parseMetadata(String metadataJson) {
        if (metadataJson == null || metadataJson.trim().isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(metadataJson, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing metadata JSON: {}", metadataJson, e);
            return new HashMap<>();
        }
    }
}