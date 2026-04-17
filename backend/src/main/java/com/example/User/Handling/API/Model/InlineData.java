package com.example.User.Handling.API.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InlineData {

    @JsonProperty("mime_type")
    private String mime_type;
    private String data;

}
