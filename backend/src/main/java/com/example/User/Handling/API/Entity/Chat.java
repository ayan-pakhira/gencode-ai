package com.example.User.Handling.API.Entity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Document(collection = "chats")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    private ObjectId id;

    private String chatName;
    private Instant createdAt;
    private Instant updatedAt;

    @DBRef
    private List<Message> messages = new ArrayList<>();
}
