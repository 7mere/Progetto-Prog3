package com.unito.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        scene.getStylesheets().add(getClass().getResource("login.css").toExternalForm());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            javafx.application.Platform.exit();
            System.exit(0); // Forza lo spegnimento di tutti i thread di rete
        });
        stage.show();
    }
}
