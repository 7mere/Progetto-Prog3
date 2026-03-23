package com.unito.client.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unito.client.models.EmailClient;
import com.unito.shared.models.Email;
import com.unito.shared.models.Message;
import com.unito.shared.protocol.CommandOperation;
import com.unito.shared.protocol.ProtocolConstants;
import com.unito.shared.utils.JsonSerializer;

public class MailService {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8090;

    /**
     * Interroga il server per recuperare i messaggi non ancora distribuiti.
     */
    public List<EmailClient> fetchNewEmails(String userEmail) throws Exception {
        List<EmailClient> newEmails = new ArrayList<>();

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Message request = new Message(CommandOperation.FETCH_NEW.getCode(), ProtocolConstants.STATUS_OK, userEmail);
            out.println(JsonSerializer.serialize(request));

            System.out.println("JSON mandato dal lato client: " + request);
            
            String jsonResponse = in.readLine();
            if (jsonResponse != null) {
                Message response = JsonSerializer.deserialize(jsonResponse, Message.class);

                if (response.getStatus() == ProtocolConstants.STATUS_OK && response.getData() != null) {
                    Email[] serverEmails = JsonSerializer.deserialize(response.getData(), Email[].class);

                    // Mapping dei DTO ricevuti nel modello supportato dalla View
                    for (Email pojo : serverEmails) {
                        newEmails.add(mapToClient(pojo));
                    }
                }
            }
        }
        return newEmails;
    }

    /**
     * Inoltra un nuovo messaggio al server per lo smistamento.
     */
    public boolean sendEmail(EmailClient emailClient) throws Exception {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Email pojo = mapToPOJO(emailClient);
            String emailJson = JsonSerializer.serialize(pojo);

            Message request = new Message(CommandOperation.SEND.getCode(), ProtocolConstants.STATUS_OK, emailJson);
            out.println(JsonSerializer.serialize(request));

            String jsonResponse = in.readLine();
            if (jsonResponse != null) {
                Message response = JsonSerializer.deserialize(jsonResponse, Message.class);
                return response.getStatus() == ProtocolConstants.STATUS_CREATED;
            }
        }
        return false;
    }

    /**
     * Richiede al server l'eliminazione definitiva di un messaggio specifico.
     */
    public boolean deleteEmail(String userEmail, String emailId) throws Exception {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Creazione del payload composito per l'operazione di delete
            String[] payload = { userEmail, emailId };
            Message request = new Message(CommandOperation.DELETE.getCode(), ProtocolConstants.STATUS_OK, JsonSerializer.serialize(payload));

            out.println(JsonSerializer.serialize(request));

            String jsonResponse = in.readLine();
            if (jsonResponse != null) {
                Message resp = JsonSerializer.deserialize(jsonResponse, Message.class);
                return resp.getStatus() == ProtocolConstants.STATUS_OK;
            }
        }
        return false;
    }

    // --- UTILITY METODI DI MAPPING ---

    private EmailClient mapToClient(Email pojo) {
        EmailClient client = new EmailClient();
        client.setId(pojo.getId());
        client.setSender(pojo.getSender());
        client.setSubject(pojo.getSubject());
        client.setBody(pojo.getBody());
        client.setDate(pojo.getDate() != null ? pojo.getDate().toString() : "");
        client.setRecipients(pojo.getRecipients());
        return client;
    }

    private Email mapToPOJO(EmailClient client) {
        Email pojo = new Email();
        pojo.setId(client.getId());
        pojo.setSender(client.getSender());
        pojo.setSubject(client.getSubject());
        pojo.setBody(client.getBody());
        pojo.setRecipients(client.getRecipients());

        if (client.getDate() != null && !client.getDate().isEmpty()) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                pojo.setDate(formatter.parse(client.getDate()));
            } catch (Exception e) {
                pojo.setDate(new Date()); // Se c'è errore, mette la data di adesso
            }
        } else {
            pojo.setDate(new Date()); // Se è vuota, mette la data di adesso
        }
        return pojo;
    }
}