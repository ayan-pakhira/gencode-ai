package com.example.User.Handling.API.Controllers;
import com.example.User.Handling.API.Model.StatusResponse;
import com.example.User.Handling.API.Services.JwtService;
import com.example.User.Handling.API.Services.StatusCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @Autowired
    private StatusCacheService statusCacheService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getStatus(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam String chatId,
                                       @RequestParam String messageId){
        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        String status = statusCacheService.getStatus(chatId, messageId);

        if(status.equals("UNKNOWN")){
            status = "PENDING";
        }

        return ResponseEntity.ok(new StatusResponse(
                userId, chatId, messageId, status
        ));
    }
}
