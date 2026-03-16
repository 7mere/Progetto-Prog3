package com.unito.client.shared.protocol;

/**
 * Elenco di tutti i possibili comandi che possono essere inviati tra Client e Server
 */
public enum CommandOperation {
    // Autentificazione per l'utente
    LOGIN("LOGIN"),
    
    //LOGOUT("LOGOUT"),
    //REGISTER("REGISTER"),

    // Operazioni dell'Email
    SEND("SEND"),
    DELETE("DELETE"),
    MARK_READ("READ"),
    FETCH_NEW("FETCH"), // Richiede le email nuove.
    LIST("LIST"), // Richiede la lista delle email.
    
    //GET_INBOX("GET_INBOX"),
    //GET_EMAIL("GET_EMAIL"),

    // Server communication
    PING("PING"), // Serve per verificare se il server è vivo.
    QUIT("QUIT"),
    ERROR("ERROR"),
    SUCCESS("SUCCESS");
    
    //DELIVERED("delivered"),
    //FAILED("failed");
    //ACK("ACK"),

    private final String code;

    CommandOperation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Ottieni CommandOperation dal codice stringa.
     */
    public static CommandOperation fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }
        for (CommandOperation op : CommandOperation.values()) {
            if (op.code.equalsIgnoreCase(code)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown command: " + code);
    }
}
