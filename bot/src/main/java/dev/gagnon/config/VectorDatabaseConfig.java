package dev.gagnon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class VectorDatabaseConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(openAiApiKey);
    }

    @Bean
    public ChatClient chatClient(OpenAiApi openAiApi) {
        return new OpenAiChatClient(openAiApi);
    }

    @Bean
    public EmbeddingClient embeddingClient(OpenAiApi openAiApi) {
        return new OpenAiEmbeddingClient(openAiApi);
    }

    // ObjectMapper bean is already defined in JacksonConfig
    // Removed duplicate to avoid BeanDefinitionOverrideException
}