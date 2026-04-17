package com.example.User.Handling.API.Services;

import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Model.RegisterRequest;
import com.example.User.Handling.API.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    //registering an user
    public User saveEntry(User user){

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        return userRepository.save(user);
    }


    //to get the users
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }


    //update the users


    public void deleteAllUser(){
        userRepository.deleteAll();
    }
}
