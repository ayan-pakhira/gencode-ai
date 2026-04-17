package com.example.User.Handling.API.Model;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class StatusResponse {

    private String userId;
    private String chatId;
    private String messageId;
    private String status;
}
