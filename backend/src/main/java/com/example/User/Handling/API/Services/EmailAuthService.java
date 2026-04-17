package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Entity.EmailAuth;
import com.example.User.Handling.API.Repositories.EmailAuthRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class EmailAuthService {

    @Autowired
    private EmailAuthRepository emailAuthRepository;

    @Autowired
    private JavaMailSender javaMailSender;


    public void sendMessages(String toUser, String body){
        try{

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toUser);
            message.setText(body);

            javaMailSender.send(message);
        }catch(Exception e){
            throw new RuntimeException("error in sending mail");
        }

        EmailAuth emailAuth = new EmailAuth(toUser, body);
        //emailAuthRepository.save(emailAuth);
        //enable the above comment if required

    }

}
