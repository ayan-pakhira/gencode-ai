package com.example.User.Handling.API.Repositories;

import com.example.User.Handling.API.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    User findByEmail(String email);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
