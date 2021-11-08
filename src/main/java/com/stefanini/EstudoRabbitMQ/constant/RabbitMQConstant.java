package com.stefanini.EstudoRabbitMQ.constant;

public enum RabbitMQConstant {
	
	FILA_ESTOQUE("ESTOQUE"),
	FILA_PRECO("PRECO");

	private String description;

	RabbitMQConstant(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	
}
