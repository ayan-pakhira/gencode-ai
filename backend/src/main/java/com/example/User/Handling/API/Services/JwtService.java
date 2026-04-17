package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Repositories.UserRepository;
import com.example.User.Handling.API.Utils.JwtUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    public ObjectId extractUserId(String authHeader){
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        if(!userRepository.existsById(new ObjectId(userId))){
            throw new RuntimeException("user not found for JWT");
        }

        return new ObjectId(userId);
    }
}
