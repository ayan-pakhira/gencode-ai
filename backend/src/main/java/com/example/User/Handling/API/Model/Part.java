package com.example.User.Handling.API.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class Part {

    private String text;

    @JsonProperty("inline_data")
    private InlineData inline_Data;

    public static Part fromText(String text){
        Part part = new Part();
        part.setText(text);
        return part;
    }

    public static Part fromImage(String mimeType, String base64){
        Part part = new Part();

        InlineData data = new InlineData(mimeType, base64);
        part.setInline_Data(data);

        return part;
    }

    public static Part fromBytes(byte[] bytes, String mimeType){
        Part part = new Part();

        part.setInline_Data(new InlineData(
                mimeType,
                Base64.getEncoder().encodeToString(bytes)
        ));

        return part;
    }
}
