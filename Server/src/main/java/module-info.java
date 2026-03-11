module com.unito.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    // requires com.fasterxml.jackson.datatype.jdk8;

    opens com.unito.server to javafx.fxml;
    opens com.unito.server.models to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.unito.server.shared.models to com.fasterxml.jackson.databind;
    opens com.unito.server.shared.protocol to com.fasterxml.jackson.databind;

    exports com.unito.server;
    exports com.unito.server.models;
}