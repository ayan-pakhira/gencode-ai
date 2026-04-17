package com.example.User.Handling.API.Model;
import lombok.*;

import java.util.*;

@Data
@Getter
@Setter
@Builder
public class RegisterRequest {

    private String id;
    private String userName;
    private String email;
    private String password;
}
