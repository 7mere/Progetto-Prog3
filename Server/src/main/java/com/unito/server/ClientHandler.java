package com.unito.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.unito.server.models.ServerStorage;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ServerStorage model;

    private BufferedReader reader; // reader per leggere i messaggi dal client
    private PrintWriter writer; // writer per inviare i messaggi al client

    public ClientHandler(Socket clientSocket, ServerStorage model) {
        this.clientSocket = clientSocket;
        this.model = model;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true); // true per abilitare l'auto-flush, così ogni volta che scriviamo qualcosa con writer.println() viene inviato immediatamente al client senza dover chiamare writer.flush() manualmente
            
        } catch (Exception e) {
            e.printStackTrace();
            // aggiunta un logger per loggare l'errore invece di stampare su console e inviare al client un messaggio di errore prima di chiudere la connessione
            System.err.println("Error initializing ClientHandler: " + e.getMessage());
        }
    }

}