package com.example.nonprofit.dto;

public class EventCommunicationRequest {

    private String subject;
    private String message;
    private String type;

    public EventCommunicationRequest() {
    }

    public EventCommunicationRequest(String subject, String message, String type) {
        this.subject = subject;
        this.message = message;
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

