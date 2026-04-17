package com.example.User.Handling.API.Controllers;
import com.example.User.Handling.API.Entity.Chat;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Filter.JwtFilter;
import com.example.User.Handling.API.Model.ChatDTO;
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

import javax.print.DocFlavor;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    //create new chat controller
    @PostMapping("/create-chat")
    public ResponseEntity<?> createNewChat(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody ChatDTO dto){
        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        Chat chat = chatService.createChat(userId, dto.getChatName());

        ChatDTO createdChat = new ChatDTO();
        createdChat.setChatId(chat.getId().toHexString());
        createdChat.setChatName(dto.getChatName());
        createdChat.setCreatedAt(Instant.now());
        createdChat.setUpdatedAt(Instant.now());

        return ResponseEntity.ok(createdChat);
    }

   //fetch chat list.
    @GetMapping("/get-chat")
    public ResponseEntity<?> getChatList(@RequestHeader("Authorization") String authHeader){

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<Chat> chats = chatService.getChats(userId);

       List<ChatDTO> chatList = chats.stream()
               .map(ChatDTO::new)
               .toList();

        return ResponseEntity.ok(chatList);

    }

    //delete chat
    @DeleteMapping("/delete-chat/{chatId}")
    public ResponseEntity<?> deleteChat(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable String chatId){
        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        chatService.deleteChatByUser(userId, chatId);

        return ResponseEntity.ok("chat deleted successfully");
    }
}
