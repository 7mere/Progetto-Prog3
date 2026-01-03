module com.unito.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.unito.client to javafx.fxml;
    exports com.unito.client;
}