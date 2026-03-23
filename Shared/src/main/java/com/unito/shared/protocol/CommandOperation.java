package com.unito.shared.protocol;

/**
 * Elenco di tutti i possibili comandi che possono essere inviati tra Client e Server
 */
public enum CommandOperation {
    // Autenticazione per l'utente
    LOGIN("LOGIN"),

    // Operazioni dell'Email
    SEND("SEND"),
    DELETE("DELETE"),
    FETCH_NEW("FETCH");// Richiede le email nuove.

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
