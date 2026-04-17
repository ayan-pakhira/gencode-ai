package com.example.User.Handling.API.Services;

import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Model.UserPrincipal;
import com.example.User.Handling.API.Repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        if(user == null){
            throw new RuntimeException("user not found with id: " + userId);
        }
        return new UserPrincipal(user);
    }
}
