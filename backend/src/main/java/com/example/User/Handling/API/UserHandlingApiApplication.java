package com.example.User.Handling.API;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class UserHandlingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserHandlingApiApplication.class, args);
	}

}
