package com.unito.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.unito.server.models.ServerStorage;
import com.unito.server.ClientHandler; // classe che gestisce la logica di comunicazione con i client, da implementare

public class ServerSocketManager extends Thread {
    private final int port;
    private final ServerStorage model; // modello condiviso tra i thread per accedere allo stato persistente e alla logica di dominio del server

    private ServerSocket serverSocket; // socket del server per accettare connessioni
    private final ThreadPoolExecutor threadPool; // pool di thread per gestire le connessioni dei client in modo efficiente

    public ServerSocketManager(int port, ServerStorage storage) { 
        // Inizializzazione del thread pool
        threadPool = new ThreadPoolExecutor(10, 15, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()); 
        threadPool.allowCoreThreadTimeOut(true); // Permette ai thread core (quei 10) di terminare se inattivi, perché costa meno creare nuovi thread che mantenerli in vita quando non servono
        
        this.port = port;
        this.model = storage;
        // Set thread as daemon so it won't prevent JVM shutdown
        setDaemon(true); // Imposta questo thread come daemon (verrà generato durante il metodo super.start()), così quando il thread principale (GUI) si chiude, anche questo thread si chiuderà automaticamente, evitando di lasciare il server in esecuzione in background se la GUI viene chiusa.
    }

    // avvia prima il metodo start() del thread, per aggiungere la logica di inizializzazione del server, e poi esegue il metodo run() che contiene la logica di accettazione delle connessioni dei client senza bloccare la GUI o altre operazioni del server
    // il metodo start() viene chiamato dal thread principale (GUI) quando si avvia il server, e il metodo run() viene eseguito in un thread separato creato da start().
    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            // aggiunta un logger per loggare l'avvio del server invece di stampare su console
            System.out.println("Server started on port " + port);
        }
        catch (IOException e) {
            // aggiunta un logger per loggare l'errore invece di stampare su console
            System.err.println("Error starting ServerSocketManager: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        super.start(); // chiama il metodo run(), funziona perché è stato sovrascritto, se non lo facessimo chiamerebbe il metodo run() della classe Thread che non fa nulla (cioè non eseguirebbe la logica di accettazione delle connessioni dei client), quindi è necessario chiamare super.start() per avviare il thread e far eseguire il metodo run() in parallelo al thread principale (GUI).
    }

    @Override
    public void run() {
        try {
            serverSocket.setSoTimeout(1000); // Imposta un timeout di 1 secondo per accettare le connessioni, così il thread non rimane bloccato indefinitamente su accept() e può controllare periodicamente se è stato richiesto di fermarsi
            serverSocket.setReuseAddress(true); // Permette di riutilizzare l'indirizzo del server anche se è in TIME_WAIT, utile per riavviare il server rapidamente
            while(!Thread.interrupted()) { // verifica se questo thread è stato interrotto da un altro thread (es. quando la GUI viene chiusa)
                try {
                    Socket clientSocket = serverSocket.accept(); // accetta una connessione da un client, se non arriva nessuna connessione entro il timeout di 1 secondo, lancia una SocketTimeoutException che viene catturata e gestita nel catch sottostante
                    threadPool.execute(new ClientHandler(clientSocket, model)); // gestisce la connessione del client in un thread separato, passando il socket del client e il modello condiviso
                }
                catch (SocketTimeoutException e) {
                    // Timeout scaduto, nessuna connessione accettata, continua a controllare se il thread è stato interrotto
                    continue;
                }
                catch (IOException e) {
                    // aggiunta un logger per loggare l'errore invece di stampare su console
                    System.err.println("Error accepting client connection on ServerSocketManager: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            // se il thread è stato interrotto (ad esempio quando la GUI viene chiusa), esce dal ciclo e procede a chiudere il server socket e il thread pool nel blocco finally
        }
        catch (IOException e) {
            // aggiunta un logger per loggare l'errore invece di stampare su console
            System.err.println("Error in ServerSocketManager run loop: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            // chiude il server socket e il thread pool quando il thread viene interrotto o si verifica un errore
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                // aggiunta un logger per loggare l'errore invece di stampare su console
                System.err.println("Error closing ServerSocket in ServerSocketManager: " + e.getMessage());
                e.printStackTrace();
            }
            threadPool.shutdown(); // chiude il thread pool, non accetterà più nuovi task ma continuerà a eseguire quelli già in coda
            try {
                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) { // aspetta fino a 30 secondi che tutti i thread del pool terminino, se dopo 30 secondi ci sono ancora thread attivi, forza la chiusura
                    threadPool.shutdownNow(); // forza la chiusura del thread pool, interrompe tutti i thread attivi
                }
            } catch (InterruptedException e) {
                // aggiunta un logger per loggare l'errore invece di stampare su console
                System.err.println("Error waiting for ThreadPool shutdown in ServerSocketManager: " + e.getMessage());
                e.printStackTrace();
                threadPool.shutdownNow(); // forza la chiusura del thread pool se il thread è stato interrotto durante l'attesa
            }
            finally {
                // aggiunta un logger per loggare l'arresto del server invece di stampare su console
                System.out.println("ServerSocketManager stopped.");
            }
        }
    }
}
