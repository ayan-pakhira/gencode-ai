package com.example.User.Handling.API.Services;

public class RateLimitExceedException extends RuntimeException{

    public RateLimitExceedException(String message){
        super(message);
    }
}
