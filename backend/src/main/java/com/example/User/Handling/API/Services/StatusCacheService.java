package com.example.User.Handling.API.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatusCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String buildKey(String chatId, String messageId){

        return "ai:status:" + chatId + ":" + messageId;
    }

    public void setPending(String chatId, String messageId){
        redisTemplate.opsForValue().set(buildKey(chatId, messageId), "PENDING");
    }

    public void setProcessing(String chatId, String messageId){
        redisTemplate.opsForValue().set(buildKey(chatId, messageId), "PROCESSING");
    }

    public void setCompleted(String chatId, String messageId){
        redisTemplate.opsForValue().set(buildKey(chatId, messageId), "COMPLETED");
    }

    public void setFailed(String chatId, String messageId){
        redisTemplate.opsForValue().set(buildKey(chatId, messageId), "FAILED");
    }

    public String getStatus(String chatId, String messageId){
        String status = redisTemplate.opsForValue().get(buildKey(chatId, messageId));

        return status != null ? status : "UNKNOWN";
    }
}
