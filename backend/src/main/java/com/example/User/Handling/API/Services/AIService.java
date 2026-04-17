package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AIService {

    private final Client client;

    private final WebClient geminiWebClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String SYSTEM_INSTRUCTION = """
            You are an advanced AI coding assistant.
            
            CAPABILITIES:
            - Generate code from natural language prompts
            - Review, debug, and improve pasted code
            - Generate website code from UI design images
            - Automatically choose the best programming language if not specified
            - Explain programming concepts (e.g., OOP, data structures, algorithms) clearly with examples
            
            SUPPORTED LANGUAGES:
            - Java
            - Python
            - C++
            - JavaScript
            
            SUPPORTED FRAMEWORKS:
            - Spring Boot
            - React
            - Node.js / Express
            
            STRICT RULES:
            - Only respond to coding, software development, or computer science related topics
            - Treat conceptual questions (e.g., "What is encapsulation?") as programming questions and answer them with examples
            - Never answer general or non-technical questions (e.g., history, politics, personal advice, etc.)
            - If prompt is NOT related to coding, reply exactly:
              "⚠ I can only assist with coding-related tasks."
            
            OUTPUT RULES:
            - Always generate valid, clean, production-ready code when applicable
            - Use proper fenced code blocks with language identifiers
            - Keep explanations concise and focused
            - For code review, provide:
              1. Issues
              2. Improvements
              3. Improved code (if necessary)
            """;


    //handle code generation from text input and review
    public String processTextPrompt(String userId, String userPrompt) throws Exception{

        String finalPrompt = SYSTEM_INSTRUCTION + "\n\nUSER PROMPT: \n" + userPrompt;
        String key = "ai:cache:" + userId + ":" + userPrompt.hashCode();

        String cachedResponse = redisTemplate.opsForValue().get(key);

        if(cachedResponse != null){
            System.out.println("cache hit");
            return cachedResponse;
        }

        System.out.println("cache miss");

        try{

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    finalPrompt,
                    null
            );

            String output = response.text();

            if(output == null || output.isBlank()){
                return "No output generated";
            }

            if (!output.contains("```")) {
                return "Try giving a more specific coding prompt.";
            }

            redisTemplate.opsForValue().set(key, output, 8, TimeUnit.HOURS);

            return output;

        }catch(Exception e){
            return "AI error " + e.getMessage();
        }
    }


    //handle code from image and text together.
    public String generateCodeFromImage(String base64Image, String imageType, String userPrompt) throws Exception{

        //String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        String finalPrompt = SYSTEM_INSTRUCTION + "\n\nUSER PROMPT:\n" +
                (userPrompt != null ? userPrompt : "Convert this UI design into website code.");

        try{

            //handling the dto - one by one.
            Part textPart = Part.fromText(finalPrompt);
            Part imagePart = Part.fromImage(imageType, base64Image);

            GeminiContent geminiContent = new GeminiContent(List.of(imagePart, textPart));

            GeminiRequest request = new GeminiRequest(List.of(geminiContent));


            //extracting the response from the api.
            GeminiResponse response = geminiWebClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        System.out.println("gemini error response: " + errorBody);
                                        return new RuntimeException("Gemini error: " + errorBody);
                                    }))
                    .bodyToMono(GeminiResponse.class)
                    .block();


            //Extract text from response
            if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
                return "No response from AI";
            }

            Content respGeminiContent = response.getCandidates().getFirst().getContent();
            if (respGeminiContent == null || respGeminiContent.getParts() == null || respGeminiContent.getParts().isEmpty()) {
                return "No code generated from image";
            }

            String aiCode = respGeminiContent.getParts().getFirst().getText();
            if (aiCode == null || aiCode.isBlank()) {
                return "No code generated";
            }

//            ObjectMapper mapper = new ObjectMapper();
//            System.out.println("request json: " + mapper.writeValueAsString(request));

            return aiCode;

        }catch(Exception e){
            return "AI error " + e.getMessage();
        }

    }

}

