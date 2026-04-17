package com.example.User.Handling.API.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private String id;
    private String content;
    private String sender;
    private String chatId;
    private String imageUrl;
}
