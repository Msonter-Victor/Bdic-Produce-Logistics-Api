package dev.gagnon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {
    
    private final ObjectMapper objectMapper;
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/embeddings";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    
    public float[] generateEmbedding(String text) {
        try {
            String requestBody = String.format("""
                {
                    "input": "%s",
                    "model": "text-embedding-ada-002"
                }
                """, text.replace("\"", "\\\""));
            
            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("OpenAI API error: {}", response.body().string());
                    throw new RuntimeException("OpenAI API call failed: " + response.code());
                }
                
                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                JsonNode embeddingNode = jsonNode.get("data").get(0).get("embedding");
                
                float[] embedding = new float[embeddingNode.size()];
                for (int i = 0; i < embeddingNode.size(); i++) {
                    embedding[i] = (float) embeddingNode.get(i).asDouble();
                }
                
                return embedding;
            }
        } catch (IOException e) {
            log.error("Error generating embedding for text: {}", text, e);
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
    
    public String generateChatResponse(String prompt, String userMessage) {
        try {
            String requestBody = String.format("""
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [
                        {"role": "system", "content": "%s"},
                        {"role": "user", "content": "%s"}
                    ],
                    "temperature": 0.7,
                    "max_tokens": 500
                }
                """, 
                prompt.replace("\"", "\\\"").replace("\n", "\\n"),
                userMessage.replace("\"", "\\\""));
            
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("OpenAI Chat API error: {}", response.body().string());
                    return "I'm sorry, I'm having trouble processing your request right now.";
                }
                
                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.get("choices").get(0).get("message").get("content").asText();
            }
        } catch (IOException e) {
            log.error("Error generating chat response", e);
            return "I'm sorry, I encountered an error. Please try again.";
        }
    }
}