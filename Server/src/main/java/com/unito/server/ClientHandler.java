package com.unito.server;


import com.unito.server.models.ServerStorage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request = in.readLine();

            if (request != null) {
                System.out.println("Richiesta ricevuta dal client: " + request);

                if (request.startsWith("LOGIN_CLICK|")) {
                    String email = request.substring("LOGIN_CLICK|".length());
                    System.out.println("L'utente " + email + " ha cliccato su login");
                    out.println("OK");
                } else {
                    System.out.println("Comando non riconosciuto");
                    out.println("ERR");
                }
            }

        } catch (Exception e) {
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