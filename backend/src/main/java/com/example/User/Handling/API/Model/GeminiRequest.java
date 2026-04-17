package com.example.User.Handling.API.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeminiRequest {

    @JsonProperty("contents")
    private List<GeminiContent> contents;
}
