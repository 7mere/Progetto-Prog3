module com.unito.client {
    requires javafx.controls;
    requires javafx.fxml;

    // 1. Diciamo al modulo che usiamo la libreria Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.unito.client to javafx.fxml;

    // 2. Apriamo le cartelle condivise a Jackson così può trasformarle in JSON
    opens com.unito.client.shared.protocol to com.fasterxml.jackson.databind;
    opens com.unito.client.shared.models to com.fasterxml.jackson.databind;
    opens com.unito.client.models to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.unito.client;
}