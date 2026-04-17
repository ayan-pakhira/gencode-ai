package com.example.User.Handling.API.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResponseDTO {

    private boolean Success;
    private String message;

    private MessageDTO userMessage;
    private String messageId;

}
