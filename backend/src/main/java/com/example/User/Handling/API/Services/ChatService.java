package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Entity.Chat;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Repositories.ChatRepository;
import com.example.User.Handling.API.Repositories.MessageRepository;
import com.example.User.Handling.API.Repositories.UserRepository;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;



    //create a new chat
   public Chat createChat (String userId, String chatName){

       User user = userRepository.findById(new ObjectId(userId))
               .orElseThrow(() -> new RuntimeException("user not found"));

       Chat newChat = new Chat();
       newChat.setChatName(chatName);
       newChat.setCreatedAt(Instant.now());
       newChat.setUpdatedAt(Instant.now());

       Chat savedChat = chatRepository.save(newChat);

       if(!user.getChatList().contains(savedChat.getId())){
           user.getChatList().add(savedChat);
       }

       userRepository.save(user);

       return savedChat;
   }


   //fetch the chat list.
    public List<Chat> getChats(String userId){
       User user = userRepository.findById(new ObjectId(userId))
               .orElseThrow(() -> new RuntimeException("user not found"));

       List<Chat> chats = user.getChatList();

       chats.sort(Comparator.comparing(Chat::getUpdatedAt).reversed());

       return chats;
    }

    //delete the group
    public void deleteChatByUser(String userId, String chatId){

       ObjectId uId = new ObjectId(userId);
       ObjectId cId = new ObjectId(chatId);

       User user = userRepository.findById(uId)
               .orElseThrow(() -> new RuntimeException("user not found"));

       Chat chat = chatRepository.findById(cId)
               .orElseThrow(() -> new RuntimeException("chat not found"));

       if(chat != null){
           user.getChatList().removeIf(x -> x.getId().equals(cId));
       }

       userRepository.save(user);
       messageRepository.deleteAllByChatId(chatId);
       chatRepository.deleteById(cId);
    }
}
