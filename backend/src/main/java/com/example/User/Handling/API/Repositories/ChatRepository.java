package com.example.User.Handling.API.Repositories;
import com.example.User.Handling.API.Entity.Chat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ChatRepository extends MongoRepository<Chat, ObjectId> {

    List<Chat> findByChatName (String chatName);

}
