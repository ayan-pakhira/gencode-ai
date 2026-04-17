package com.example.User.Handling.API.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    //limits
    private static final int MAX_REQUESTS_PER_MINUTE = 3;
    private static final int MAX_REQUESTS_PER_DAY = 5;

    public void validateRateLimit(String userId, AIActionType actionType){
        String minuteKey = buildKey(userId, actionType, "minute");
        String dayKey = buildKey(userId, actionType, "day");

        long minuteCount = increment(minuteKey, Duration.ofMinutes(1));
        long dayCount = increment(dayKey, Duration.ofDays(1));

        if(minuteCount > MAX_REQUESTS_PER_MINUTE){
            throw new RateLimitExceedException(
                    "RPM limit exceed for: " + actionType
            );
        }
        if(dayCount > MAX_REQUESTS_PER_DAY){
            throw new RateLimitExceedException(
                    "RPD limit exceed for: " + actionType
            );
        }


    }

    private long increment(String key, Duration ttl) {
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, ttl);
        }

        return count != null ? count : 0;
    }

    private String buildKey(String userId, AIActionType action, String window) {
        return "rate:" + userId + ":" + action.name() + ":" + window;
    }
}
