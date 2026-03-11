package com.unito.server.shared.protocol;

/**
 * Protocol constants used for communication between Client and Server.
 * Defines message structure, delimiters, and other protocol-level configurations.
 */
public class ProtocolConstants {

    // Message structure delimiters
    public static final String MESSAGE_DELIMITER = "||END||";
    public static final String FIELD_DELIMITER = "|";
    public static final String PAYLOAD_FIELD = "payload";
    public static final String COMMAND_FIELD = "command";
    public static final String STATUS_FIELD = "status";
    public static final String DATA_FIELD = "data";

    // HTTP-like status codes for protocol communication
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    //public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    //public static final int STATUS_CONFLICT = 409;
    public static final int STATUS_SERVER_ERROR = 500;

    // JSON content type
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CHARSET = "UTF-8";

    // Timeout values (in milliseconds)
    public static final long CONNECTION_TIMEOUT = 30000;  // 30 seconds
    public static final long READ_TIMEOUT = 60000;        // 60 seconds
    public static final long WRITE_TIMEOUT = 30000;       // 30 seconds

    // Message size limits
    public static final int MAX_MESSAGE_SIZE = 10485760;  // 10 MB
    public static final int DEFAULT_BUFFER_SIZE = 8192;   // 8 KB

    // Authentication
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_BEARER_PREFIX = "Bearer ";

    private ProtocolConstants() {
        // Serve a impedire la creazione di oggetti. (istanze di questa classe)
        // Perché la classe è pensata solo per costanti statiche.
    }
}
