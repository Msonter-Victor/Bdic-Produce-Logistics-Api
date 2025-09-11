package dev.gagnon.service;

import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {
    
    private final OpenAIService openAIService;
    
    public PGvector generateEmbedding(String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Text cannot be null or empty");
            }
            
            float[] embedding = openAIService.generateEmbedding(text.trim());
            return new PGvector(embedding);
        } catch (Exception e) {
            log.error("Error generating embedding for text: {}", text, e);
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
    
    public String embeddingToString(PGvector embedding) {
        if (embedding == null) {
            return null;
        }
        
        float[] vector = embedding.toArray();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        sb.append("]");
        return sb.toString();
    }
    
    public double calculateSimilarity(PGvector embedding1, PGvector embedding2) {
        if (embedding1 == null || embedding2 == null) {
            return 0.0;
        }
        
        float[] vec1 = embedding1.toArray();
        float[] vec2 = embedding2.toArray();
        
        if (vec1.length != vec2.length) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}