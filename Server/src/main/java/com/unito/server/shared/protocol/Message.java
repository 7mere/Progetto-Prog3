package com.unito.server.shared.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic message wrapper for client-server communication.
 * All messages exchanged follow this structure.
 */
public class Message {
    @JsonProperty("command")
    private String command;

    @JsonProperty("status")
    private int status;

    @JsonProperty("data")
    private String data;  // JSON payload

    @JsonProperty("timestamp")
    private long timestamp;

    // Empty constructor for Jackson
    public Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String command, int status, String data) {
        this.command = command;
        this.status = status;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "command='" + command + '\'' +
                ", status=" + status +
                ", data='" + data + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
