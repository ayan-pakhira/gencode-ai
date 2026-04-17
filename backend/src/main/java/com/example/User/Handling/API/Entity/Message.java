package com.example.User.Handling.API.Entity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Message {

    @Id
    private ObjectId id;

    private String senderId;
    private String chatId;
    private String content;
    private String imageUrl;

    private Date timeStamp;


}
