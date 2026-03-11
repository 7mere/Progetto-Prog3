package com.unito.client.service;

import com.unito.client.models.EmailClient;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MailService {

    // Indirizzo e porta del vostro Server (da accordare col compagno!)
    private static final String SERVER_IP = "127.0.0.1"; // Localhost per ora
    private static final int SERVER_PORT = 8090;

    /**
     * Metodo per chiedere al server le nuove mail.
     * @param userEmail L'email dell'utente attualmente loggato
     * @return Una lista di nuove email
     */
    public List<EmailClient> fetchNewEmails(String userEmail) throws Exception {
        List<EmailClient> newEmails = new ArrayList<>();

        // 1. Apro una connessione "usa e getta" (come richiesto dalle specifiche)
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 2. Mando la richiesta al Server (Es. formato JSON o testo semplice)
            // In attesa della libreria del vostro compagno, facciamo finta di mandare un comando:
            out.println("FETCH|" + userEmail);

            // 3. Leggo la risposta dal Server
            String response = in.readLine();

            // Qui in futuro trasformerete il JSON ricevuto (response) in oggetti EmailClient!
            // Per ora lasciamo la lista vuota, la riempiremo quando il Server sarà pronto.
        }
        // Il blocco try-with-resources chiude il Socket in automatico alla fine!

        return newEmails;
    }

    /**
     * Metodo per inviare una mail.
     */
    public boolean sendEmail(EmailClient email) throws Exception {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Anche qui, manderete il vostro JSON
            out.println("SEND|" + email.getSender() + "|" + email.getSubject());

            String response = in.readLine();
            return "OK".equals(response);
        }
    }
}
