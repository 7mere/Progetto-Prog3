module pp3.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens pp3.server to javafx.fxml;
    exports pp3.server;
}