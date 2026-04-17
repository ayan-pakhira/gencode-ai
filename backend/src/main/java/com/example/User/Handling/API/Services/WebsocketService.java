package com.example.User.Handling.API.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebsocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendStatusUpdate(String chatId, String messageId, String status){

        Map<String, String> payload = new HashMap<>();
        payload.put("chatId", chatId);
        payload.put("messageId", messageId);
        payload.put("status", status);

        messagingTemplate.convertAndSend("/topic/status", payload);
    }
}
