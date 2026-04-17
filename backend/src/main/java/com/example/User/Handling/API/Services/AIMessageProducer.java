package com.example.User.Handling.API.Services;
import com.example.User.Handling.API.Config.RabbitMQConfig;
import com.example.User.Handling.API.Model.AIQueueRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(AIQueueRequest request){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                request
        );
    }
}
