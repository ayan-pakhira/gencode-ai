package com.example.User.Handling.API.Controllers;
import com.example.User.Handling.API.Entity.Chat;
import com.example.User.Handling.API.Entity.Message;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Repositories.ChatRepository;
import com.example.User.Handling.API.Repositories.MessageRepository;
import com.example.User.Handling.API.Repositories.UserRepository;
import com.example.User.Handling.API.Services.ChatService;
import com.example.User.Handling.API.Services.JwtService;
import com.example.User.Handling.API.Services.MessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/create-message")
    public ResponseEntity<?> createMessage(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Map<String, String> body){

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User sender = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        String chatId = body.get("chatId");
        String content = body.get("content");

        if(chatId == null || chatId.isBlank()){
            throw new RuntimeException("chatId is required");
        }

        Chat chat = chatRepository.findById(new ObjectId(chatId))
                .orElseThrow(() -> new RuntimeException("chat not found with this id"));

        Message message = new Message();
        message.setSenderId(sender.getId().toHexString());
        message.setChatId(chatId);
        message.setContent(content);

        Message savedMessage = messageService.saveMessage(message);

        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/get-message/{chatId}")
    public ResponseEntity<?> getMessages(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable String chatId){
        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        Chat chat = chatRepository.findById(new ObjectId(chatId))
                .orElseThrow(() -> new RuntimeException("chat not found"));

        List<Message> messages = messageService.fetchMessages(chatId);

        List<Map<String, Object>> response = messages.stream().map(msg -> Map.<String, Object>of(
                "id", msg.getId(),
                "senderId", msg.getSenderId(),
                "chatId", msg.getChatId(),
                "content", msg.getContent()
        )).toList();

        return ResponseEntity.ok(messages);
    }
}