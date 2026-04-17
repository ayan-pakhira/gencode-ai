package com.example.User.Handling.API.Model;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AIResponse {

    private boolean success;
    private String output;
}
