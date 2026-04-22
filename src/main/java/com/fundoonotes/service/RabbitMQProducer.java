package com.fundoonotes.service;

import com.fundoonotes.event.EmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.key.email}")
    private String emailRoutingKey;

    @Value("${rabbitmq.routing.key.reminder}")
    private String reminderRoutingKey;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /*
     * Publishes an email event to RabbitMQ.
     * The call returns INSTANTLY — we don't wait for the email to be sent.
     * RabbitMQ holds the message until the consumer picks it up.
     *
     * convertAndSend(exchange, routingKey, message)
     * - exchange: where to send it
     * - routingKey: how to route it to the right queue
     * - message: the actual payload (converted to JSON by our MessageConverter)
     */
    public void sendEmailEvent(EmailEvent event) {
        log.info("Publishing email event to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(exchange, emailRoutingKey, event);
        log.info("Email event published successfully for: {}", event.getToEmail());
    }

    // You can add more publish methods here later for reminders, etc.
    public void sendReminderEvent(Object reminderEvent) {
        log.info("Publishing reminder event to RabbitMQ");
        rabbitTemplate.convertAndSend(exchange, reminderRoutingKey, reminderEvent);
    }
}