module com.unito.client {
    requires javafx.controls;
    requires javafx.fxml;
    
    // uso della libreria Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    // requires com.fasterxml.jackson.datatype.jdk8;
    
    // uso del modulo Shared (shared.jar)
    requires com.unito.shared;

    opens com.unito.client to javafx.fxml;
    opens com.unito.client.models to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.unito.client;
}