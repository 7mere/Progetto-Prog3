package com.unito.server;

import com.unito.server.ServerSocketManager;
import com.unito.server.models.ServerStorage;

import java.util.Properties;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {

    private ServerSocketManager server; // classe che implementerà e gestirà i socket dal lato server
    private ServerStorage storage; // model che permette di contenere lo stato persistente e la logica di dominio

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // possibilà di aggiungere un css per lo stile della GUI oppure aggiungerlo in un altro modo? (DA VEDERE)

        // aggiungere un controller

        stage.setTitle("SERVER EMAIL");
        stage.setScene(scene);

        // possibilità di fare un arresto graduale
        stage.setOnCloseRequest(ev -> {
            // if (server != null) server.stop();
            Platform.exit();
            System.exit(0);
        });

        stage.show();

        // carica la configurazione del server da file
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties")));

        // Uso di un ExecutorService per usare un solo thread
        // ExecutorService execServer = Executors.newSingleThreadExecutor();
        // execServer.submit(new ServerSocketManager(Integer.parseInt(properties.getProperty("port")), storage)); // aggiungere come parametro il controller??

        // Altro metodo per avviare il server in un thread separato
        server = new ServerSocketManager(Integer.parseInt(properties.getProperty("port")), storage); // aggiungere come parametro il controller??
        server.start(); // avvia il server in un thread separato (cioè esegue il metodo run() in parallelo al thread principale)

    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
