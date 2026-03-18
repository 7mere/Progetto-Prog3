module com.unito.shared {
    // uso della libreria Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    // espone i package condivisi
    exports com.unito.shared.models;
    exports com.unito.shared.protocol;
    exports com.unito.shared.utils;

    // apre i package necessari a Jackson
    opens com.unito.shared.models to com.fasterxml.jackson.databind;
    opens com.unito.shared.protocol to com.fasterxml.jackson.databind;
}
