package com.example.User.Handling.API.Controllers;

import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Repositories.UserRepository;
import com.example.User.Handling.API.Services.JwtService;
import com.example.User.Handling.API.Services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    //update the user details.
    //--todo: need to be fixed
    @PutMapping("/update-user")
    public ResponseEntity<?> editUser(@RequestBody User user,
                                      @RequestHeader("Authorization") String authHeader){

        ObjectId userId = jwtService.extractUserId(authHeader);

        User userInDb = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        userInDb.setUserName(user.getUserName());
        userInDb.setPassword(encoder.encode(user.getPassword()));
        userInDb.setEmail(user.getEmail());
        return null;
    }

}
