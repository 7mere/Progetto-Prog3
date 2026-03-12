package com.unito.server;

import com.unito.server.models.ServerStorage;
import com.unito.server.shared.models.Email;
import com.unito.server.shared.protocol.CommandOperation;
import com.unito.server.shared.protocol.Message;
import com.unito.server.shared.protocol.ProtocolConstants;
import com.unito.server.shared.utils.JsonSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

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
            // Leggiamo la stringa inviata dal Client
            String requestJson = in.readLine();

            if (requestJson != null) {
                // 1. TRADUZIONE: Trasformiamo il JSON in un oggetto Message
                Message request = JsonSerializer.deserialize(requestJson, Message.class);
                CommandOperation command = CommandOperation.fromCode(request.getCommand());

                // Log visivo sulla console del Server!
                HelloController.getInstance().logMessage("Ricevuto comando " + command.getCode() + " da un client.");

                // Prepariamo la busta per la risposta
                Message response = new Message();
                response.setCommand(command.getCode());

                // 2. LO SWITCH: Decidiamo cosa fare in base al comando
                switch (command) {
                    case LOGIN:
                        String userEmail = request.getData();
                        response.setStatus(ProtocolConstants.STATUS_OK);
                        response.setData("Login effettuato con successo");
                        HelloController.getInstance().logMessage("Utente loggato: " + userEmail);
                        break;

                    case FETCH_NEW:
                        String userToFetch = request.getData();

                        // USIAMO IL NUOVO METODO FILTRATO
                        List<Email> inbox = model.getNewEmails(userToFetch);

                        response.setStatus(ProtocolConstants.STATUS_OK);
                        response.setData(JsonSerializer.serialize(inbox));
                        break;

                    case SEND:
                        // Estraiamo l'oggetto Email dal campo 'data'
                        Email emailToSend = JsonSerializer.deserialize(request.getData(), Email.class);

                        // Salviamo la mail nel file di ogni destinatario
                        for (String recipient : emailToSend.getRecipients()) {
                            model.addEmailToInbox(recipient.trim(), emailToSend);
                        }

                        response.setStatus(ProtocolConstants.STATUS_OK);
                        response.setData("Email elaborata con successo");
                        HelloController.getInstance().logMessage("Email inviata da " + emailToSend.getSender() + " a " + emailToSend.getRecipients());
                        break;

                    case DELETE:
                        // Il client manda un array JSON: ["email_utente", "id_mail"]
                        String[] data = JsonSerializer.deserialize(request.getData(), String[].class);
                        String user = data[0];
                        String id = data[1];

                        boolean ok = model.deleteEmail(user, id);

                        response.setStatus(ok ? ProtocolConstants.STATUS_OK : ProtocolConstants.STATUS_BAD_REQUEST);
                        response.setData(ok ? "Eliminata" : "Errore: mail non trovata");
                        break;

                    default:
                        // Se arriva un comando che non conosciamo (es. DELETE che faremo più avanti)
                        response.setStatus(ProtocolConstants.STATUS_BAD_REQUEST);
                        response.setData("Comando non supportato");
                        HelloController.getInstance().logMessage("Comando ignorato: " + command);
                        break;
                }

                // 3. RISPOSTA: Inviamo il pacchetto di ritorno al Client
                out.println(JsonSerializer.serialize(response));
            }

        } catch (Exception e) {
            HelloController.getInstance().logMessage("Errore di rete con il Client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close(); // Chiudiamo sempre il socket alla fine (Usa e Getta)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}