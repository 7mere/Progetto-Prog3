package pp3.server.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Email {
    private String id; // ID univoco, assegnato dal server
    private String sender;
    private List<String> recipients;
    private String subject;
    private String body;
    private Date date;
    // private Boolean read; //Usato solo dal client, permette di capire se l'utente ha letto l'email oppure no

    // costruttore vuoto per Jackson (deserializzazione)
    public Email() {}

    public Email(
            String sender,
            List<String> recipients,
            String subject,
            String body) {
        if(sender.isEmpty()) {throw new IllegalArgumentException("Sender cannot be empty");}
        this.id = UUID.randomUUID().toString();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return id.equals(email.id);
    }
}
