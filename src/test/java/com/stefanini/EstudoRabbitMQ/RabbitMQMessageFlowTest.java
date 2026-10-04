package com.stefanini.EstudoRabbitMQ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.stefanini.EstudoRabbitMQ.service.RabbitMQMessageService;

@Testcontainers
@SpringBootTest
class RabbitMQMessageFlowTest {

    @Container
    static final RabbitMQContainer rabbitMq = new RabbitMQContainer("rabbitmq:3.13-management");

    @DynamicPropertySource
    static void configureRabbitMq(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMq::getHost);
        registry.add("spring.rabbitmq.port", () -> String.valueOf(rabbitMq.getAmqpPort()));
        registry.add("spring.rabbitmq.username", rabbitMq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMq::getAdminPassword);
    }

    @Autowired
    private com.stefanini.EstudoRabbitMQ.service.RabbitMQMessageService rabbitMQMessageService;

    @Test
    void shouldPublishMessageThroughService() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitMQMessageService service = new RabbitMQMessageService(rabbitTemplate);

        service.sendMessage("ESTOQUE", "produto-123");

        verify(rabbitTemplate).convertAndSend("amq.direct", "ESTOQUE", "produto-123");
    }

    @Test
    void shouldExposeRestEndpointsForMessageFlow() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new com.stefanini.EstudoRabbitMQ.controller.RabbitMQMessageController(rabbitMQMessageService)
        ).build();

        mockMvc.perform(post("/api/mensagens/ESTOQUE")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"produto-123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.queue").value("ESTOQUE"))
            .andExpect(jsonPath("$.message").value("produto-123"));

        var result = mockMvc.perform(get("/api/mensagens/ESTOQUE"))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(result.getResponse().getContentAsString())
            .contains("\"queue\":\"ESTOQUE\"")
            .contains("\"message\":\"produto-123\"");
    }
}
