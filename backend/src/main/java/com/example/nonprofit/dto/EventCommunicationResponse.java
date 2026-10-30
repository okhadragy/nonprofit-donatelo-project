package com.example.nonprofit.dto;

import java.util.Date;
import java.util.List;

public class EventCommunicationResponse {

    private Long eventId;
    private String eventTitle;
    private String subject;
    private String message;
    private String type;
    private int recipientsCount;
    private List<String> recipientNames;
    private Date sentAt;

    public EventCommunicationResponse() {
    }

    public EventCommunicationResponse(
            Long eventId,
            String eventTitle,
            String subject,
            String message,
            String type,
            int recipientsCount,
            List<String> recipientNames,
            Date sentAt) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.recipientsCount = recipientsCount;
        this.recipientNames = recipientNames;
        this.sentAt = sentAt;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
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

    public int getRecipientsCount() {
        return recipientsCount;
    }

    public void setRecipientsCount(int recipientsCount) {
        this.recipientsCount = recipientsCount;
    }

    public List<String> getRecipientNames() {
        return recipientNames;
    }

    public void setRecipientNames(List<String> recipientNames) {
        this.recipientNames = recipientNames;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}

