package com.unito.client.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.ArrayList;
import java.util.List;

public class EmailClient {

    // Invece delle normali String, usiamo le StringProperty per il pattern Observer
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty sender = new SimpleStringProperty();
    private final StringProperty subject = new SimpleStringProperty();
    private final StringProperty body = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();

    // Per i destinatari multipli, una semplice lista va bene
    private List<String> recipients = new ArrayList<>();

    // Costruttore vuoto (molto utile in futuro quando userete Jackson per i JSON)
    public EmailClient() {}

    // Costruttore di comodità per creare email velocemente (utile ora per i test)
    public EmailClient(String id, String sender, String subject, String body, String date) {
        this.id.set(id);
        this.sender.set(sender);
        this.subject.set(subject);
        this.body.set(body);
        this.date.set(date);
    }

    // --- METODI GETTER E SETTER STANDARD ---
    // Usiamo .get() e .set() per estrarre o inserire il valore nella "scatola" Property

    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value); }

    public String getSender() { return sender.get(); }
    public void setSender(String value) { sender.set(value); }

    public String getSubject() { return subject.get(); }
    public void setSubject(String value) { subject.set(value); }

    public String getBody() { return body.get(); }
    public void setBody(String value) { body.set(value); }

    public String getDate() { return date.get(); }
    public void setDate(String value) { date.set(value); }

    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }

    // --- METODI PROPERTY (FONDAMENTALI PER JAVAFX E L'OBSERVER PATTERN) ---
    // Questi metodi restituiscono la "scatola" stessa. Serviranno alla TableView!

    public StringProperty idProperty() { return id; }
    public StringProperty senderProperty() { return sender; }
    public StringProperty subjectProperty() { return subject; }
    public StringProperty bodyProperty() { return body; }
    public StringProperty dateProperty() { return date; }
}