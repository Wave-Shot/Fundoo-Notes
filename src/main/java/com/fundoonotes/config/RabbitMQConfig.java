package com.fundoonotes.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // These values come from application.properties
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.email}")
    private String emailQueue;

    @Value("${rabbitmq.queue.reminder}")
    private String reminderQueue;

    @Value("${rabbitmq.routing.key.email}")
    private String emailRoutingKey;

    @Value("${rabbitmq.routing.key.reminder}")
    private String reminderRoutingKey;

    /*
     * Queue declaration.
     * true = durable: the queue survives a RabbitMQ restart.
     * If false, queue disappears when RabbitMQ restarts and messages are lost.
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueue, true);
    }

    @Bean
    public Queue reminderQueue() {
        return new Queue(reminderQueue, true);
    }

    /*
     * DirectExchange — routes messages to queues based on exact routing key match.
     * Other types exist (Topic, Fanout) but Direct is the simplest and most common.
     */
    @Bean
    public DirectExchange fundooExchange() {
        return new DirectExchange(exchange);
    }

    /*
     * Binding — connects a queue to an exchange with a routing key.
     * This says: "messages sent to fundoo.exchange with routing key
     * email.routingkey should go into email.queue"
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange fundooExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(fundooExchange)
                .with(emailRoutingKey);
    }

    @Bean
    public Binding reminderBinding(Queue reminderQueue, DirectExchange fundooExchange) {
        return BindingBuilder
                .bind(reminderQueue)
                .to(fundooExchange)
                .with(reminderRoutingKey);
    }

    /*
     * MessageConverter — tells RabbitMQ to serialize/deserialize messages as JSON.
     * Without this, RabbitMQ uses Java serialization which is brittle and unreadable.
     * With this, messages in the queue look like: {"email":"user@gmail.com","name":"Test"}
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /*
     * RabbitTemplate is what you use to SEND messages (like JdbcTemplate for RabbitMQ).
     * We attach our JSON converter to it so all messages go out as JSON automatically.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}