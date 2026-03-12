package com.unito.client.shared.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * model Email condiviso tra Client e Server
 * Questa classe è serializzata a JSON per la comunicazione
 */

public class Email {
    private String id; // ID univoco, assegnato dal server
    private String sender; // indirizzo email del mittente
    private List<String> recipients; // lista di indirizzi email dei destinatari, può essere più di uno per supportare CC e BCC in futuro
    private String subject; // oggetto dell'email
    private String body; // corpo dell'email
    private Date date; // data di invio dell'email
    // private Boolean read; //Usato solo dal client, permette di capire se l'utente ha letto l'email oppure no

    // costruttore vuoto per Jackson (deserializzazione)
    public Email() {}

    public Email(
            String sender,
            List<String> recipients,
            String subject,
            String body) {
        if(sender.isEmpty() || sender == null) {throw new IllegalArgumentException("Sender cannot be empty");}
        this.id = UUID.randomUUID().toString(); // VEDERE SE FARLO QUI O AL SERVER 
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.date = new Date();
        // this.read = false;
    }

    // getter e setter
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getSender() {return sender;}
    public void setSender(String sender) {this.sender = sender;}
    public List<String> getRecipients() {return recipients;}
    public void setRecipients(List<String> recipients) {this.recipients = recipients;}
    public String getSubject() {return subject;}
    public void setSubject(String subject) {this.subject = subject;}
    public String getBody() {return body;}
    public void setBody(String body) {this.body = body;}
    public Date getDate() {return date;}
    public void setDate(Date date) {this.date = date;}

    @Override
    public String toString() {
        return "Email{" + "id='" + id + '\'' + ", sender='" + sender + '\'' + ", subject='" + subject + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) { // per ottenere un metodo più robusto durante il confronto degli id  
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return id != null && id.equals(email.id);
    }
    
    /*
     @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    */
}