package com.unito.shared.protocol;

/**
 * Protocol constants used for communication between Client and Server.
 * Defines message structure, delimiters, and other protocol-level configurations.
 */
public class ProtocolConstants {

    // codice di status per protocollo di comunicazione
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;

    private ProtocolConstants() {
        // Serve a impedire la creazione di oggetti. (istanze di questa classe)
        // Perché la classe è pensata solo per costanti statiche.
    }
}
