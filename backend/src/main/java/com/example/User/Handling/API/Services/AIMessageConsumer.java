package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Config.RabbitMQConfig;
import com.example.User.Handling.API.Entity.Message;
import com.example.User.Handling.API.Model.AIQueueRequest;
import com.example.User.Handling.API.Model.Base64MultipartFile;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class AIMessageConsumer {

    @Autowired
    private AIService aiService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private StatusCacheService statusCacheService;

    @Autowired
    private WebsocketService websocketService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(AIQueueRequest request){


        String chatId = request.getChatId();
        String messageId = request.getMessageId();
        try{

            String aiOutput;


            System.out.println("consumer hit!!");
            statusCacheService.setProcessing(chatId, messageId);
            websocketService.sendStatusUpdate(chatId, messageId, "PROCESSING");

            if(request.getImageBase64() != null){

                aiOutput = aiService.generateCodeFromImage(
                        request.getImageBase64(),
                        request.getImageType(),
                        request.getUserPrompt()
                );
            }else{
                aiOutput = aiService.processTextPrompt(
                        request.getUserId(),
                        request.getUserPrompt()
                );
            }

            System.out.println("AI output generated");

            Message aiMessage = new Message();
            aiMessage.setSenderId("AI");
            aiMessage.setChatId(request.getChatId());
            aiMessage.setContent(aiOutput);

            messageService.saveMessage(aiMessage);

            statusCacheService.setCompleted(chatId, messageId);
            websocketService.sendStatusUpdate(chatId, messageId, "COMPLETED");

            System.out.println("AI message saved");

        }catch(Exception e){
            statusCacheService.setFailed(chatId, messageId);
            websocketService.sendStatusUpdate(chatId, messageId, "FAILED");
            throw new RuntimeException(e.getMessage());
        }
    }
}
