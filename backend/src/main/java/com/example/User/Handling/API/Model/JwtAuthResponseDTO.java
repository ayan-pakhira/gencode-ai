package com.example.User.Handling.API.Model;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Data
@Getter
@Setter
@Builder
public class JwtAuthResponseDTO {

    private String accessToken;
    private String refreshToken;


    public JwtAuthResponseDTO(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
