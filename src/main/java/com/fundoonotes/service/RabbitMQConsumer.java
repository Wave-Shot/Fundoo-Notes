package com.fundoonotes.service;

import com.fundoonotes.event.EmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);

    /*
     * @RabbitListener — Spring constantly watches this queue in a background thread.
     * The moment a message arrives, this method is called automatically.
     *
     * The parameter type (EmailEvent) tells Spring to deserialize the JSON
     * message back into an EmailEvent object for you.
     *
     * Right now we just log it. In a real project you'd inject a
     * JavaMailSender here and actually send the email.
     */
    @RabbitListener(queues = "${rabbitmq.queue.email}")
    public void consumeEmailEvent(EmailEvent event) {
        log.info("============================================");
        log.info("EMAIL EVENT RECEIVED FROM RABBITMQ");
        log.info("To      : {}", event.getToEmail());
        log.info("Name    : {}", event.getUserName());
        log.info("Subject : {}", event.getSubject());
        log.info("Body    : {}", event.getBody());
        log.info("Type    : {}", event.getEventType());
        log.info("============================================");

        // TODO in Part 3: inject JavaMailSender and actually send the email
        // For now, logging confirms the async pipeline is working
    }
}