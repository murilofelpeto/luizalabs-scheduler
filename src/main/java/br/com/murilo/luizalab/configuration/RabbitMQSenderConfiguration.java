package br.com.murilo.luizalab.configuration;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQSenderConfiguration {

    public static final String EXCHANGE_SENDER_NAME = "ex.magalu.sender";
    public static final String EXCHANGE_LISTENER_NAME = "ex.magalu.listener";

    @Bean
    public Exchange declareSenderExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_SENDER_NAME).durable(true).build();
    }

    @Bean
    public Exchange declareListenerExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_LISTENER_NAME).durable(true).build();
    }
}
