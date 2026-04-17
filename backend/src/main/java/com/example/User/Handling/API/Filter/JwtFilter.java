package com.example.User.Handling.API.Filter;
import com.example.User.Handling.API.Entity.User;
import com.example.User.Handling.API.Model.UserPrincipal;
import com.example.User.Handling.API.Repositories.UserRepository;
import com.example.User.Handling.API.Services.CustomUserDetailsService;
import com.example.User.Handling.API.Utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String path = request.getRequestURI();
        System.out.println("JWT FILTER - Incoming path: " + path);


        String authorizationHeader = request.getHeader("Authorization");
        String userId = null;
        String jwt = null;

//        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
//            jwt = authorizationHeader.substring(7);
//            userId = jwtUtil.extractUserId(jwt);
//        }
//
//        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
//            User user = userRepository.findById(new ObjectId(userId)).orElse(null);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
//            if(jwtUtil.validateToken(jwt)){
//                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
            userId = jwtUtil.extractUserId(jwt);

            if(userId != null){
                User user = userRepository.findById(new ObjectId(userId)).orElse(null);

                if(user != null && jwtUtil.validateToken(jwt)){
                    UserDetails userDetails = new UserPrincipal(user);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
