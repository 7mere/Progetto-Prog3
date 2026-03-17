package com.unito.server.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unito.server.HelloController;
import com.unito.server.shared.models.Email;
import com.unito.server.shared.utils.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerStorage {

    // La cartella dove salveremo i file JSON (si creerà in automatico nel progetto)
    private static final String STORAGE_DIR = "server_data/";
    private static final String USERS_FILE = STORAGE_DIR + "users.json";

    // Lock per garantire coerenza di accesso al file users.json
    private final ReentrantReadWriteLock usersLock = new ReentrantReadWriteLock();

    // Lock per ogni casella di posta (mailbox) in modo da non bloccare tutto il server
    private final Map<String, Object> mailboxLocks = new ConcurrentHashMap<>();

    public ServerStorage() {
        // Quando il server parte, crea la cartella se non esiste
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            HelloController.getInstance().logMessage("Creata cartella di storage: " + STORAGE_DIR);
        }

        // Inizializza account preconfigurati (se non esistono) + relative caselle
        ensureDefaultUsers();
    }

    private Object getMailboxLock(String userEmail) {
        return mailboxLocks.computeIfAbsent(userEmail, k -> new Object());
    }

    private void ensureDefaultUsers() {
        Map<String, User> users = loadUsers();
        boolean changed = false;

        // Definisci qui gli account preconfigurati
        List<String> defaults = List.of(
            "luca.gado@edu.unito.it",
            "mehratab.istifanos@edu.unito.it",
            "valerio.ghirardotto@edu.unito.it"
        );

        for (String email : defaults) {
            if (!users.containsKey(email)) {
                users.put(email, new User(email));
                changed = true;
            }
        }

        if (changed) {
            saveUsers(users);
            HelloController.getInstance().logMessage("Account preconfigurati creati/aggiornati");
        }

        // Assicuriamo che le caselle dei preconfigurati esistano (file JSON vuoti)
        for (String email : defaults) {
            ensureInboxExists(email);
        }
    }

    private void ensureInboxExists(String userEmail) {
        File inbox = new File(STORAGE_DIR + userEmail + ".json");
        if (!inbox.exists()) {
            synchronized (getMailboxLock(userEmail)) {
                if (!inbox.exists()) {
                    try {
                        Files.write(Path.of(inbox.getPath()), "[]".getBytes());
                    } catch (IOException e) {
                        HelloController.getInstance().logMessage("Errore creazione casella per " + userEmail + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private Map<String, User> loadUsers() {
        usersLock.readLock().lock();
        try {
            File f = new File(USERS_FILE);
            if (!f.exists()) {
                return new HashMap<>();
            }
            return JsonSerializer.getObjectMapper().readValue(f, new TypeReference<Map<String, User>>() {});
        } catch (IOException e) {
            HelloController.getInstance().logMessage("Errore lettura users.json: " + e.getMessage());
            return new HashMap<>();
        } finally {
            usersLock.readLock().unlock();
        }
    }

    private void saveUsers(Map<String, User> users) {
        usersLock.writeLock().lock();
        try {
            File f = new File(USERS_FILE);
            JsonSerializer.getObjectMapper().writeValue(f, users);
        } catch (IOException e) {
            HelloController.getInstance().logMessage("Errore salvataggio users.json: " + e.getMessage());
        } finally {
            usersLock.writeLock().unlock();
        }
    }

    public boolean validateUser(String email, String password) {
        usersLock.readLock().lock();
        try {
            return loadUsers().containsKey(email);
        } finally {
            usersLock.readLock().unlock();
        }
    }

    public boolean userExists(String email) {
        usersLock.readLock().lock();
        try {
            return loadUsers().containsKey(email);
        } finally {
            usersLock.readLock().unlock();
        }
    }

    public boolean createUser(String email) {
        usersLock.writeLock().lock();
        try {
            Map<String, User> users = loadUsers();
            if (users.containsKey(email)) return false;
            users.put(email, new User(email));
            saveUsers(users);
            ensureInboxExists(email);
            return true;
        } finally {
            usersLock.writeLock().unlock();
        }
    }

    /**
     * Legge la casella di posta di un utente.
     */
    public List<Email> loadUserEmails(String userEmail) {
        synchronized (getMailboxLock(userEmail)) {
            File userFile = new File(STORAGE_DIR + userEmail + ".json");

            if (!userFile.exists()) {
                return new ArrayList<>(); // Se il file non esiste, la casella è vuota
            }

            try {
                return JsonSerializer.getObjectMapper().readValue(userFile, new TypeReference<List<Email>>() {});
            } catch (IOException e) {
                HelloController.getInstance().logMessage("Errore lettura file per " + userEmail + ": " + e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    /**
     * Salva l'intera casella di posta di un utente.
     */
    private void saveUserEmails(String userEmail, List<Email> emails) {
        synchronized (getMailboxLock(userEmail)) {
            File userFile = new File(STORAGE_DIR + userEmail + ".json");
            try {
                JsonSerializer.getObjectMapper().writeValue(userFile, emails);
            } catch (IOException e) {
                HelloController.getInstance().logMessage("Errore salvataggio file per " + userEmail + ": " + e.getMessage());
            }
        }
    }

    /**
     * Quando un utente INVIA una mail, questo metodo la aggiunge al file del DESTINATARIO.
     */
    public boolean addEmailToInbox(String recipient, Email newEmail) {
        synchronized (getMailboxLock(recipient)) {
            List<Email> inbox = loadUserEmails(recipient);
            inbox.add(newEmail);
            saveUserEmails(recipient, inbox);
            HelloController.getInstance().logMessage("Salvata nuova mail in arrivo per: " + recipient);
            return true;
        }
    }

    public boolean deleteEmail(String userEmail, String idToDelete) {
        synchronized (getMailboxLock(userEmail)) {
            List<Email> emails = loadUserEmails(userEmail);
            boolean removed = emails.removeIf(e -> e.getId() != null && e.getId().equals(idToDelete));

            if (removed) {
                saveUserEmails(userEmail, emails);
                HelloController.getInstance().logMessage("Mail eliminata da: " + userEmail);
            }
            return removed;
        }
    }

    /**
     * Restituisce solo le email che non sono ancora state inviate al client.
     */
    public List<Email> getNewEmails(String userEmail) {
        synchronized (getMailboxLock(userEmail)) {
            List<Email> allEmails = loadUserEmails(userEmail);
            List<Email> toSend = new ArrayList<>();

            for (Email e : allEmails) {
                if (!e.isDistributed()) {
                    toSend.add(e);
                    e.setDistributed(true);
                }
            }

            if (!toSend.isEmpty()) {
                saveUserEmails(userEmail, allEmails);
            }
            return toSend;
        }
    }

    private static class User {
        private String email;

        public User() { }

        public User(String email) {
            this.email = email;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}