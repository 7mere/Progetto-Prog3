module com.unito.server {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.unito.server to javafx.fxml;
    exports com.unito.server;
    exports com.unito.server.models;
    opens com.unito.server.models to javafx.fxml;
}