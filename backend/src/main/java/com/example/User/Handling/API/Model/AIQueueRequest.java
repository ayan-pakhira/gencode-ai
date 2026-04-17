package com.example.User.Handling.API.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIQueueRequest {

    private String userId;
    private String chatId;
    private String messageId;
    private String userPrompt;
    private String imageBase64;
    private String imageType;

    private String imageUrl;
}
