package com.example.User.Handling.API.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

    @JsonProperty("content")
    private Content content;
}
