package com.unito.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

import com.unito.shared.models.Message;
import com.unito.shared.protocol.CommandOperation;
import com.unito.shared.protocol.ProtocolConstants;
import com.unito.shared.utils.JsonSerializer;

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

        if (isValidEmail(insertedMail)) {
            // Autenticazione lato server
            boolean serverAccepted = notifyServerLoginClick(insertedMail);

            if (!serverAccepted) {
                result.setText("Utente non autorizzato dal server.");
                return;
            }

            try {
                // Inizializzazione della vista principale (Inbox)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("inbox-view.fxml"));
                Scene inboxScene = new Scene(loader.load(), 1100, 720);
                inboxScene.getStylesheets().add(getClass().getResource("inbox.css").toExternalForm());

                // Passaggio del contesto utente al controller successivo
                InboxController inboxController = loader.getController();
                inboxController.initUser(insertedMail);

                // Sostituzione della scena nello stage corrente
                Stage stage = (Stage) mailField.getScene().getWindow();
                stage.setScene(inboxScene);
                stage.setTitle("Mail Client - " + insertedMail);
            } catch (Exception e) {
                System.err.println("Errore fatale nel caricamento della UI: " + e.getMessage());
                result.setText("Errore di inizializzazione dell'interfaccia.");
            }
        } else {
            result.setText("Formato email non valido.");
            mailField.clear();
        }
    }

    /**
     * Valida sintatticamente l'indirizzo email tramite espressione regolare.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String normalized = email.trim();
        if (normalized.isEmpty()) return false;
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, normalized);
    }

    /**
     * Invia la richiesta di LOGIN al server tramite socket.
     * Ritorna true se il server risponde con STATUS_OK.
     */
    private boolean notifyServerLoginClick(String email) {
        try (
                Socket socket = new Socket("127.0.0.1", 8090);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Message request = new Message(CommandOperation.LOGIN.getCode(), ProtocolConstants.STATUS_OK, email);
            out.println(JsonSerializer.serialize(request));

            String jsonResponse = in.readLine();
            if (jsonResponse == null) return false;

            Message response = JsonSerializer.deserialize(jsonResponse, Message.class);
            return response.getStatus() == ProtocolConstants.STATUS_OK;

        } catch (Exception e) {
            System.err.println("Connessione al server fallita: " + e.getMessage());
            return false;
        }
    }
}