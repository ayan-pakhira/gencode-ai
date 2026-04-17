package com.example.User.Handling.API.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient geminiWebClient(){

        return WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
