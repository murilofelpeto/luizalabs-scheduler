package br.com.murilo.luizalab.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQListenerConfiguration {

    public static final String QUEUE_SENDER =  "ex.magalu.sender";
    public static final String QUEUE_LISTENER = "ex.magalu.listener";

    public static final String EXCHANGE_SENDER_NAME = "ex.magalu.sender";
    public static final String EXCHANGE_LISTENER_NAME = "ex.magalu.listener";

    public static final String ROUTING_KEY_SENDER = "MGL.SENDER.DLQ";
    public static final String ROUTING_KEY_LISTENER = "MGL.LISTENER.DLQ";

    @Bean(name = "exchangeSender")
    public Exchange declareExchangeSender() {
        return ExchangeBuilder.directExchange(EXCHANGE_SENDER_NAME).durable(true).build();
    }

    @Bean(name = "exchangeListener")
    public Exchange declareExchangeListener() {
        return ExchangeBuilder.directExchange(EXCHANGE_LISTENER_NAME).durable(true).build();
    }

    @Bean(name = "queueSender")
    public Queue declareQueueSender() {
        return QueueBuilder.durable(QUEUE_SENDER).build();
    }

    @Bean(name = "queueListener")
    public Queue declareQueueListener() {
        return QueueBuilder.durable(QUEUE_LISTENER).build();
    }

    @Bean(name = "bindingSender")
    public Binding declareBindingSender(Queue queueSender, Exchange exchangeSender) {
        return BindingBuilder.bind(queueSender).to(exchangeSender).with(ROUTING_KEY_SENDER).noargs();
    }

    @Bean(name = "bindingListener")
    public Binding declareBindingListener(Queue queueListener, Exchange exchangeListener) {
        return BindingBuilder.bind(queueListener).to(exchangeListener).with(ROUTING_KEY_SENDER).noargs();
    }
}
