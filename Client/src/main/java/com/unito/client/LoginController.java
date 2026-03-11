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
                Socket socket = new Socket("127.0.0.1", 8090);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println("LOGIN_CLICK|" + email);

            String response = in.readLine();

            if (!"OK".equals(response)) {
                throw new RuntimeException("Il server ha risposto in modo non valido.");
            }

        } catch (Exception e) {
            System.err.println("Errore comunicazione client-server: " + e.getMessage());

        }
    }
    }


