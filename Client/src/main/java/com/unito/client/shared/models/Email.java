package com.unito.client.shared.models;

import java.util.Date;
import java.util.List;

/**
 * Email model shared between Client and Server.
 * This class is serialized to JSON for communication.
 */
public class Email {
    private String id;              // Unique ID assigned by the server
    private String sender;          // Sender email address
    private List<String> recipients; // List of recipient email addresses
    private String subject;         // Email subject
    private String body;            // Email body
    private Date date;              // Email send date

    // Empty constructor for Jackson deserialization
    public Email() {}

    /**
     * Constructor for creating a new Email
     */
    public Email(String sender, List<String> recipients, String subject, String body) {
        if (sender == null || sender.isEmpty()) {
            throw new IllegalArgumentException("Sender cannot be empty");
        }
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.date = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", subject='" + subject + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return id != null && id.equals(email.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
