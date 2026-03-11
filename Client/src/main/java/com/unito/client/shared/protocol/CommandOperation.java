package com.unito.client.shared.protocol;

/**
 * Enumeration of all possible commands that can be sent
 * between Client and Server.
 */
public enum CommandOperation {
    // User authentication
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    REGISTER("REGISTER"),

    // Email operations
    SEND_EMAIL("SEND_EMAIL"),
    GET_INBOX("GET_INBOX"),
    GET_EMAIL("GET_EMAIL"),
    DELETE_EMAIL("DELETE_EMAIL"),
    MARK_READ("MARK_READ"),

    // Server communication
    ACK("ACK"),
    ERROR("ERROR"),
    SUCCESS("SUCCESS");

    private final String code;

    CommandOperation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get CommandOperation from string code.
     * Case-insensitive.
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
