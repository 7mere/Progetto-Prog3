package com.unito.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class LoginController {
    @FXML
    private TextField mailField;
    @FXML
    private Label result;
    @FXML
    protected void onLoginButtonClick() {
        String insertedMail = mailField.getText();
        if(isValidEmail(insertedMail)) {
            try {
                // 1. Carica la vista della Inbox
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("inbox-view.fxml"));
                javafx.scene.Scene inboxScene = new javafx.scene.Scene(loader.load(), 1100, 720);
                inboxScene.getStylesheets().add(getClass().getResource("inbox.css").toExternalForm());

                // ---- ECCO LE DUE RIGHE FONDAMENTALI CHE MANCAVANO ----
                InboxController inboxController = loader.getController();
                inboxController.initUser(insertedMail);
                // -------------------------------------------------------

                // 2. Cambia la scena nella finestra attuale
                javafx.stage.Stage stage = (javafx.stage.Stage) mailField.getScene().getWindow();
                stage.setScene(inboxScene);
                stage.setTitle("Mail Client - " + insertedMail);
                notifyServerLoginClick(insertedMail);

            } catch (Exception e) {
                e.printStackTrace();
                result.setText("Errore nel caricamento della Inbox.");
            }
        } else {
            result.setText("La mail non è valida prova con un'altra");
            mailField.clear();
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;

        String normalized = email.trim(); // rimuove spazi davanti/dietro (copi-incolla ecc.)
        if (normalized.isEmpty()) return false;

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, normalized);
    }

    private void notifyServerLoginClick(String email) {
        try (
                java.net.Socket socket = new java.net.Socket("127.0.0.1", 8090);
                java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))
        ) {
            com.unito.client.shared.protocol.Message request = new com.unito.client.shared.protocol.Message(
                    com.unito.client.shared.protocol.CommandOperation.LOGIN.getCode(),
                    com.unito.client.shared.protocol.ProtocolConstants.STATUS_OK,
                    email
            );
            out.println(com.unito.client.shared.utils.JsonSerializer.serialize(request));

            String jsonResponse = in.readLine();
            if (jsonResponse != null) {
                com.unito.client.shared.protocol.Message response = com.unito.client.shared.utils.JsonSerializer.deserialize(jsonResponse, com.unito.client.shared.protocol.Message.class);
                if (response.getStatus() != com.unito.client.shared.protocol.ProtocolConstants.STATUS_OK) {
                    throw new RuntimeException("Il server ha rifiutato il login.");
                }
            } else {
                throw new RuntimeException("Nessuna risposta dal server.");
            }
        } catch (Exception e) {
            System.err.println("Errore comunicazione client-server: " + e.getMessage());
        }
    }
}


