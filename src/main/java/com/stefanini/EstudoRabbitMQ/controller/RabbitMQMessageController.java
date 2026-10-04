package com.stefanini.EstudoRabbitMQ.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stefanini.EstudoRabbitMQ.service.RabbitMQMessageService;

@RestController
@RequestMapping("/api/mensagens/{queue}")
public class RabbitMQMessageController {

    private final RabbitMQMessageService rabbitMQMessageService;

    public RabbitMQMessageController(RabbitMQMessageService rabbitMQMessageService) {
        this.rabbitMQMessageService = rabbitMQMessageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> send(@PathVariable String queue,
            @RequestBody Map<String, String> payload) {
        String message = payload.getOrDefault("message", "");
        String sentMessage = rabbitMQMessageService.sendMessage(queue, message);
        return ResponseEntity.ok(Map.of("queue", queue, "message", sentMessage));
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> receive(@PathVariable String queue) {
        String message = rabbitMQMessageService.consumeMessage(queue);
        return ResponseEntity.ok(Map.of("queue", queue, "message", message == null ? "" : message));
    }
}
