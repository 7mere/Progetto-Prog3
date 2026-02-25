package com.unito.server;

import com.unito.server.models.ServerStorage;

public class ServerSocketManager extends Thread {
    private final int port;
    private final ServerStorage model;

    public ServerSocketManager(int port, ServerStorage storage) {
        this.port = port;
        this.model = storage;
        // Set thread as daemon so it won't prevent JVM shutdown
        setDaemon(true);
    }

    @Override
    public void run() {

    }
}
