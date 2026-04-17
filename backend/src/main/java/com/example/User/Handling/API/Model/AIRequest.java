package com.example.User.Handling.API.Model;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIRequest {

    private String chatId;
    private String userPrompt;
    private MultipartFile image;

}
