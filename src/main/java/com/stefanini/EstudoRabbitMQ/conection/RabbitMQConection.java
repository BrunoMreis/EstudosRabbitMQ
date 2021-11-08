package com.stefanini.EstudoRabbitMQ.conection;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.stefanini.EstudoRabbitMQ.constant.RabbitMQConstant;

@Component
public class RabbitMQConection {
	private static final String NAME_EXCHENGE = "amq.direct";

	private AmqpAdmin amqpAdmin;

	
	public RabbitMQConection(AmqpAdmin amqpAdmin) {
		this.amqpAdmin = amqpAdmin;
	}

	private Queue queue(String nomeQueue) {
		return new Queue(nomeQueue, true, false, false);
	}

	private DirectExchange Exchange() {
		return new DirectExchange(NAME_EXCHENGE);

	}

	private Binding binding(Queue queue, DirectExchange exchange) {
		return new Binding(NAME_EXCHENGE, Binding.DestinationType.QUEUE, exchange.getName(), queue.getName(), null);
	}

	@Autowired
	private void add() {
		
		Queue queueEstoque = this.queue(RabbitMQConstant.FILA_ESTOQUE.getDescription());
		Queue queuePreco = this.queue(RabbitMQConstant.FILA_PRECO.getDescription());

		DirectExchange exchange = this.Exchange();

		Binding relationshipPreco = this.binding(queuePreco, exchange);
		Binding relationshipEstoque = this.binding(queueEstoque, exchange);

		this.binding(queuePreco, exchange);

		this.amqpAdmin.declareQueue(queuePreco);
		this.amqpAdmin.declareQueue(queueEstoque);

		this.amqpAdmin.declareBinding(relationshipEstoque);
		this.amqpAdmin.declareBinding(relationshipPreco);

		this.amqpAdmin.declareExchange(exchange);
	}

}
