package pp3.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("SERVER EMAIL");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket();
        server.start(); // avvia il server in un thread separato
        launch(args);
    }
}
