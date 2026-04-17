package com.example.User.Handling.API.Config;
import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiClientConfig {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Bean
    public Client geminiClient(){
        if(apiKey == null && apiKey.isBlank()){
            throw new IllegalArgumentException("Gemini API key is missing");
        }

        return Client.builder()
                .apiKey(apiKey)
                .build();
    }
}
