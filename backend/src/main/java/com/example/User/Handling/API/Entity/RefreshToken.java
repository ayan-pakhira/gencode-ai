package com.example.User.Handling.API.Entity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Document(collection = "refresh-token")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {


    @Id
    private String id;

    @Indexed(unique = true)
    private String refreshToken;

    private String userId;

    private Instant createdAt;

    private Date expiryAt;


    private boolean revoke;

    @DBRef
    private User user;
}
