package com.example.User.Handling.API.Controllers;
import com.example.User.Handling.API.Entity.Message;
import com.example.User.Handling.API.Model.*;
import com.example.User.Handling.API.Repositories.MessageRepository;
import com.example.User.Handling.API.Services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AIController {

    @Autowired
    private StatusCacheService statusCacheService;

    @Autowired
    private AIService aiService;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AIMessageProducer producer;

    @Autowired
    private CloudinaryService cloudinaryService;


    @PostMapping("/process")
    public ResponseEntity<?> processAI(@ModelAttribute AIRequest request,
                                       @RequestHeader("Authorization")String authHeader){

        try{

            String token = authHeader.replace("Bearer ", "");
            String userId = String.valueOf(jwtService.extractUserId(token));

            if((request.getUserPrompt() == null || request.getUserPrompt().isBlank())
            && request.getImage() == null){
                return ResponseEntity.badRequest()
                        .body(new AIResponse(false, "Error: Either userPrompt or image must be provided."));
            }

            String chatId = request.getChatId();

            if(chatId == null || chatId.length() != 24){
                throw new RuntimeException("invalid chat id: " + chatId);
            }



            //Declaration of action type.
            AIActionType actionType;
            if(request.getImage() != null){
                actionType = AIActionType.IMAGE_TO_CODE;
            } else if (request.getUserPrompt().contains("review")){
                actionType = AIActionType.CODE_REVIEW;
            }else{
                actionType = AIActionType.CODE_GENERATION;
            }

            rateLimiterService.validateRateLimit(userId, actionType);

            String imageUrl = null;
            String base64Image = null;
            String imageType = null;

            if(request.getImage() != null){
                imageUrl = cloudinaryService.uploadImage(request.getImage());
                base64Image = Base64.getEncoder().encodeToString(request.getImage().getBytes());
                imageType = request.getImage().getContentType();
            }


            Message userMessage = new Message();
            userMessage.setSenderId(userId);
            userMessage.setChatId(chatId);
            userMessage.setContent(request.getUserPrompt());
            userMessage.setImageUrl(imageUrl);

            Message savedMessage = messageService.saveMessage(userMessage);

            //pending status from cache
            statusCacheService.setPending(chatId, savedMessage.getId().toHexString());


            //creating request for sending to producer
            AIQueueRequest queueRequest = new AIQueueRequest(
                    userId,
                    chatId,
                    savedMessage.getId().toHexString(),
                    request.getUserPrompt(),
                    base64Image,
                    imageType,
                    imageUrl
            );


            //sending request rabbitmq producer
            producer.sendMessage(queueRequest);

            MessageDTO userDTO = new MessageDTO(
                    userMessage.getId().toHexString(),
                    userMessage.getContent(),
                    userMessage.getSenderId().equals("AI") ? "AI" : "USER",
                    userMessage.getChatId(),
                    userMessage.getImageUrl()
            );

            return ResponseEntity.ok(new AIResponseDTO(true, "Success", userDTO, savedMessage.getId().toHexString()));

        }catch(RateLimitExceedException e){

            return ResponseEntity.status(429)
                    .body(new AIResponse(false, e.getMessage()));

        }catch(Exception e){
            e.printStackTrace();
            log.error("full error", e);
            return ResponseEntity.internalServerError()
                    .body(new AIResponse(false, "AI response error " + e.getMessage()));
        }
    }

}
