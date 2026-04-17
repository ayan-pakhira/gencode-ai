package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Entity.RefreshToken;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Repositories.RefreshTokenRepository;
import com.example.User.Handling.API.Repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    public RefreshToken createToken(String userId){
        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

//        if(user == null){
//            throw new RuntimeException("User not found");
//        }

        RefreshToken token = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .userId(String.valueOf(user))
                .createdAt(Instant.now())
                .expiryAt(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60))
                .revoke(false)
                .build();

        return refreshTokenRepository.save(token);
    }


    public RefreshToken verifyToken(String refreshToken){

        RefreshToken token = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new RuntimeException("token not found"));

        if(token.isRevoke()){
            throw new RuntimeException("token has revoked");
        }

        if(token.getExpiryAt().before(new Date())){
            throw new RuntimeException("token has expired");
        }

        return token;
    }


}
