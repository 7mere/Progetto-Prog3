package com.unito.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.unito.server.models.ServerStorage;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ServerStorage model;

    public ClientHandler(Socket clientSocket, ServerStorage model) {
        this.clientSocket = clientSocket;
        this.model = model;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // reader per leggere i messaggi dal client
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true) // writer per inviare i messaggi al client
                                                                                        // true per abilitare l'auto-flush, così ogni volta che scriviamo qualcosa con writer.println() viene inviato immediatamente al client senza dover chiamare writer.flush() manualmente
        ) {
            String request = reader.readLine();

            if (request != null) {
                System.out.println("Richiesta ricevuta dal client: " + request);

                if (request.startsWith("LOGIN_CLICK|")) {
                    String email = request.substring("LOGIN_CLICK|".length());
                    System.out.println("L'utente " + email + " ha cliccato su login");
                    writer.println("OK");
                } else {
                    System.out.println("Comando non riconosciuto");
                    writer.println("ERR");
                }
            }

        } catch (Exception e) {
            // aggiunta un logger per loggare l'errore invece di stampare su console e inviare al client un messaggio di errore prima di chiudere la connessione
            System.err.println("Errore nel ClientHandler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Errore chiusura socket client: " + e.getMessage());
            }

        }
    }
}