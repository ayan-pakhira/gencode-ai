package com.example.User.Handling.API.Repositories;

import com.example.User.Handling.API.Entity.EmailAuth;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuthRepository extends MongoRepository<EmailAuth, ObjectId> {
}
