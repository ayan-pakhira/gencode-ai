package com.example.User.Handling.API.Repositories;
import com.example.User.Handling.API.Entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MessageRepository extends MongoRepository<Message, ObjectId> {

    @Query("{ 'chatId': ?0 }")
    List<Message> findConversation(String chatId);

    void deleteAllByChatId(String chatId);
}
