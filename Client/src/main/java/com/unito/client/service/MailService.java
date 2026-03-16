package com.unito.client.service;

import com.unito.client.models.EmailClient;
import com.unito.client.shared.models.Email;
import com.unito.client.shared.protocol.CommandOperation;
import com.unito.client.shared.protocol.Message;
import com.unito.client.shared.protocol.ProtocolConstants;
import com.unito.client.shared.utils.JsonSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MailService {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8090;

    /**
     * Chiede al server le nuove email.
     */
    public List<EmailClient> fetchNewEmails(String userEmail) throws Exception {
        List<EmailClient> newEmails = new ArrayList<>();

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. Creiamo il pacchetto di richiesta usando le classi condivise
            Message request = new Message(CommandOperation.FETCH_NEW.getCode(), ProtocolConstants.STATUS_OK, userEmail);
            out.println(JsonSerializer.serialize(request)); // Inviamo il JSON al server

            // 2. Leggiamo la risposta
            String jsonResponse = in.readLine();
            if (jsonResponse != null) {
                // Trasformiamo il JSON ricevuto in un oggetto Message
                Message response = JsonSerializer.deserialize(jsonResponse, Message.class);

                if (response.getStatus() == ProtocolConstants.STATUS_OK && response.getData() != null) {
                    // Estraiamo l'array di Email "semplici" dal campo data
                    Email[] serverEmails = JsonSerializer.deserialize(response.getData(), Email[].class);

                    // Le traduciamo in EmailClient per la nostra interfaccia grafica!
                    for (Email pojo : serverEmails) {
                        newEmails.add(mapToClient(pojo));
                    }
                }
            }
        }
        return newEmails;
    }

    /**
     * Invia una mail al server.
     */
    public boolean sendEmail(EmailClient emailClient) throws Exception {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. Traduciamo la nostra EmailClient in una Email "semplice" per il server
            Email pojo = mapToPOJO(emailClient);

            // 2. Impacchettiamo e inviamo
            String emailJson = JsonSerializer.serialize(pojo);
            Message request = new Message(CommandOperation.SEND.getCode(), ProtocolConstants.STATUS_OK, emailJson);
            out.println(JsonSerializer.serialize(request));

            // 3. Leggiamo se è andata a buon fine
            String jsonResponse = in.readLine();
            if (jsonResponse != null) {
                Message response = JsonSerializer.deserialize(jsonResponse, Message.class);
                return response.getStatus() == ProtocolConstants.STATUS_OK;
            }
        }
        return false;
    }

    // --- METODI "TRADUTTORI" (MAPPER) ---

    // Da Email del Server a EmailClient (per la GUI)
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

    // Da EmailClient a Email del Server (per inviarla via rete)
    private Email mapToPOJO(EmailClient client) {
        Email pojo = new Email();
        pojo.setId(client.getId());
        pojo.setSender(client.getSender());
        pojo.setSubject(client.getSubject());
        pojo.setBody(client.getBody());
        pojo.setRecipients(client.getRecipients());
        //pojo.setDate(client.getDate());
        return pojo;
    }
}