package com.example.User.Handling.API.Model;
import com.example.User.Handling.API.Entity.Chat;
import lombok.*;

import java.time.Instant;
import java.util.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDTO {

    private String chatId;
    private String chatName;
    private Instant createdAt;
    private Instant updatedAt;


    public ChatDTO(Chat chat){
        this.chatId = chat.getId().toHexString();
        this.chatName = chat.getChatName();
        this.createdAt = chat.getCreatedAt();
        this.updatedAt = chat.getUpdatedAt();

    }
}
