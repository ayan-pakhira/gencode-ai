package com.example.User.Handling.API.Config;
import com.example.User.Handling.API.Entity.RefreshToken;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Repositories.UserRepository;
import com.example.User.Handling.API.Services.RefreshTokenService;
import com.example.User.Handling.API.Utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String provider = authentication.getAuthorities().stream().findFirst()
                .map(Object::toString).orElse("GITHUB");
        String providerId = oAuth2User.getAttribute("id").toString();
        String userName = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        if(email == null){
            email = userName + "@github.com";
        }

        String finalEmail = email;

        User user = userRepository.findByProviderAndProviderId(provider, providerId).orElseGet(
                () -> {
                    User newUser = User.builder()
                            .id(new ObjectId())
                            .userName(userName)
                            .email(finalEmail)
                            .roles(List.of("USER"))
                            .provider(provider)
                            .providerId(providerId)
                            .chatList(Collections.emptyList())
                            .build();

                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(String.valueOf(user.getId()), Collections.emptyMap());

        RefreshToken refreshToken = refreshTokenService.createToken(user.getId().toString());

        Cookie cookie = new Cookie("refreshTokenAI", refreshToken.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //change it to true, while using https in prods.
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);


        response.setHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.sendRedirect(
                "https://gencodeai.cloud/oauth-success?token=" + token
        );



    }
}
