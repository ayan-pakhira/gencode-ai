package com.example.User.Handling.API.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeminiContent {

    @JsonProperty("parts")
    private List<Part> parts;
}
