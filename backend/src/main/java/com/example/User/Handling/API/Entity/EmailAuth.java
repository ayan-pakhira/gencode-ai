package com.example.User.Handling.API.Entity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "email-logs")
@Data
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EmailAuth {

    @Id
    private ObjectId id;

    private String toUser;

    private String subject;

    private String body;

    private String sentAt;

    public EmailAuth(String toUser, String body){
        this.toUser = toUser;
        this.body = body;
    }
}
