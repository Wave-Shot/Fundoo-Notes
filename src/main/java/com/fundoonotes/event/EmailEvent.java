package com.fundoonotes.event;

import java.io.Serializable;

/*
 * This class represents the data we send through RabbitMQ.
 * It must be serializable so it can be converted to JSON and back.
 * Keep it simple — only include what the consumer needs.
 */
public class EmailEvent implements Serializable {

    private String toEmail;
    private String userName;
    private String subject;
    private String body;
    private String eventType; // "WELCOME", "PASSWORD_RESET", "VERIFICATION" etc.

    // Default constructor is REQUIRED for Jackson JSON deserialization
    public EmailEvent() {}

    public EmailEvent(String toEmail, String userName,
                      String subject, String body, String eventType) {
        this.toEmail = toEmail;
        this.userName = userName;
        this.subject = subject;
        this.body = body;
        this.eventType = eventType;
    }

    // Getters and setters
    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    @Override
    public String toString() {
        return "EmailEvent{toEmail='" + toEmail + "', eventType='" + eventType + "'}";
    }
}