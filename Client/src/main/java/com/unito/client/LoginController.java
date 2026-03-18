package com.unito.client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

import com.unito.shared.protocol.CommandOperation;
import com.unito.shared.protocol.Message;
import com.unito.shared.protocol.ProtocolConstants;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField mailField;
    @FXML
    private Label result;
    @FXML
    protected void onLoginButtonClick() {
        String insertedMail = mailField.getText();
        if(isValidEmail(insertedMail)) {

            boolean serverAccepted = notifyServerLoginClick(insertedMail);

            if(!serverAccepted) {
                result.setText("Utente non riconosciuto");
                return;
            }

            try {
                // 1. Carica la vista della Inbox
                FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("inbox-view.fxml"));
                Scene inboxScene = new javafx.scene.Scene(loader.load(), 1100, 720);
                inboxScene.getStylesheets().add(getClass().getResource("inbox.css").toExternalForm());

                InboxController inboxController = loader.getController();
                inboxController.initUser(insertedMail);
                // -------------------------------------------------------

                // 2. Cambia la scena nella finestra attuale
                Stage stage = (javafx.stage.Stage) mailField.getScene().getWindow();
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

    private boolean notifyServerLoginClick(String email) {
        try (
                Socket socket = new java.net.Socket("127.0.0.1", 8090);
                PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))
        ) {
            Message request = new com.unito.shared.protocol.Message(
            CommandOperation.LOGIN.getCode(),
            ProtocolConstants.STATUS_OK,
            email
            );
            out.println(com.unito.shared.utils.JsonSerializer.serialize(request));

            String jsonResponse = in.readLine();
            if (jsonResponse == null) {
                return false;
            }

            Message response = com.unito.shared.utils.JsonSerializer.deserialize(jsonResponse, com.unito.shared.protocol.Message.class);
            
            return response.getStatus() == ProtocolConstants.STATUS_OK;

        } catch (Exception e) {
            System.err.println("Server non raggiungibile: " + e.getMessage());
            return false;
        }
    }
}


