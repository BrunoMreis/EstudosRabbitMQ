package com.stefanini.EstudoRabbitMQ.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQMessageService {

    private static final String EXCHANGE = "amq.direct";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQMessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String sendMessage(String queue, String message) {
        rabbitTemplate.convertAndSend(EXCHANGE, queue, message);
        return message;
    }

    public String consumeMessage(String queue) {
        Object payload = rabbitTemplate.receiveAndConvert(queue);
        return payload == null ? null : payload.toString();
    }
}
