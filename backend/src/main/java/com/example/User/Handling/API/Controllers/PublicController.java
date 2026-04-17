package com.example.User.Handling.API.Controllers;

import com.example.User.Handling.API.Entity.EmailAuth;
import com.example.User.Handling.API.Entity.RefreshToken;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Model.JwtAuthResponseDTO;
import com.example.User.Handling.API.Model.RegisterRequest;
import com.example.User.Handling.API.Model.UserDTO;
import com.example.User.Handling.API.Repositories.RefreshTokenRepository;
import com.example.User.Handling.API.Repositories.UserRepository;
import com.example.User.Handling.API.Services.*;
import com.example.User.Handling.API.Utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager auth;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private EmailAuthService emailAuthService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    //get users
    @GetMapping("/")
    public ResponseEntity<?> getAllUsers(){
        List<User> user = userService.getAllUsers();

        return ResponseEntity.ok(user);
    }

    //register-user
    @PostMapping("/register-user")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request){

        try{

           User user = new User();
           user.setUserName(request.getUserName());
           user.setEmail(request.getEmail());
           user.setPassword(request.getPassword());

           User savedUser = userService.saveEntry(user);

           auth.authenticate(new UsernamePasswordAuthenticationToken(
                   request.getEmail(), request.getPassword()
           ));


           String accessToken = jwtUtil.generateToken(request.getId(), new HashMap<>());
           RefreshToken refreshToken = refreshTokenService.createToken(request.getEmail());

           JwtAuthResponseDTO response = JwtAuthResponseDTO.builder()
                   .accessToken(accessToken)
                   .refreshToken(refreshToken.getRefreshToken())
                   .build();

           emailAuthService.sendMessages(request.getEmail(), "Congratulations!!! You Have Successfully Registered With Us..");

           return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e){
            throw new RuntimeException("error while registering the user!!");
        }


    }


    //login-user
    @PostMapping("/login")
   public ResponseEntity<?> loginUser(@RequestBody UserDTO user){

        try{

            auth.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getEmail(), user.getPassword()
            ));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String accessToken = jwtUtil.generateToken(user.getId(), new HashMap<>());
            RefreshToken refreshToken = refreshTokenService.createToken(user.getEmail());

            return ResponseEntity.ok(accessToken);

        }catch(Exception e){
            log.error("error while loggin in: ", e);
            throw new RuntimeException("Invalid email or password");
        }
   }

   //create access token from refresh token
    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateAccessToken(@CookieValue("refreshToken") String refreshToken){
        RefreshToken token = refreshTokenService.verifyToken(refreshToken);

        String accessToken = jwtUtil.generateToken(
                token.getUserId(),
                Collections.emptyMap()
        );

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken
        ));
    }

    //logout user
    @PostMapping("/logout")
    public ResponseEntity<?> logOutUser(HttpServletResponse response){

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("logged out successfully");
    }



    @DeleteMapping("/delete-users")
    public ResponseEntity<?> deleteAllUsers(){
        userService.deleteAllUser();
        return ResponseEntity.ok("deleted");
    }


    @DeleteMapping("/delete-tokens")
    public ResponseEntity<?> deleteAllTokens(){
        refreshTokenRepository.deleteAll();

        return ResponseEntity.ok("deleted all tokens");
    }

}
