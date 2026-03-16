package com.unito.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelloController {

    @FXML
    private ListView<String> logListView;

    // Creiamo un'istanza "Singleton" così gli altri file (come ClientHandler)
    // possono facilmente chiamare la grafica per scrivere i log!
    private static HelloController instance;

    @FXML
    public void initialize() {
        instance = this;
        logMessage("Server avviato e in ascolto...");
    }

    public static HelloController getInstance() {
        return instance;
    }

    /**
     * Metodo per aggiungere una riga di log.
     * Usa Platform.runLater perché verrà chiamato da Thread in background!
     */
    public void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String finalMessage = "[" + timestamp + "] " + message;

        // Stampiamo sulla grafica in sicurezza
        Platform.runLater(() -> {
            if (logListView != null) {
                logListView.getItems().add(finalMessage);
                // Scorre in automatico verso il basso per farti leggere l'ultima riga
                logListView.scrollTo(logListView.getItems().size() - 1);
            }
        });

        // Stampiamo anche nella console di IntelliJ per comodità
        System.out.println(finalMessage);
    }
}
