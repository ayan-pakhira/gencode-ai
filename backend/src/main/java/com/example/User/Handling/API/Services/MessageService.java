package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Entity.Chat;
import com.example.User.Handling.API.Entity.Message;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Repositories.ChatRepository;
import com.example.User.Handling.API.Repositories.MessageRepository;
import com.example.User.Handling.API.Repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public Message saveMessage(Message message){

        Chat chat = chatRepository.findById(new ObjectId(message.getChatId()))
                .orElseThrow(() -> new RuntimeException("chat not found"));

        message.setTimeStamp(new Date());
        Message storeMessage = messageRepository.save(message);

        chat.getMessages().add(storeMessage);
        chatRepository.save(chat);

        return storeMessage;
    }

    public List<Message> fetchMessages(String chatId){

        List<Message> messages = messageRepository.findConversation(chatId);

        messages.sort(Comparator.comparing(Message::getTimeStamp));

        return messages;
    }
}
