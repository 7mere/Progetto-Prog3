package com.unito.server.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unito.server.HelloController;
import com.unito.server.shared.models.Email;
import com.unito.server.shared.utils.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerStorage {

    // La cartella dove salveremo i file JSON (si creerà in automatico nel progetto)
    private static final String STORAGE_DIR = "server_data/";

    public ServerStorage() {
        // Quando il server parte, crea la cartella se non esiste
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            HelloController.getInstance().logMessage("Creata cartella di storage: " + STORAGE_DIR);
        }
    }

    /**
     * Legge la casella di posta di un utente.
     * È synchronized per evitare che qualcuno legga mentre un altro sta scrivendo.
     */
    public synchronized List<Email> loadUserEmails(String userEmail) {
        File userFile = new File(STORAGE_DIR + userEmail + ".json");

        if (!userFile.exists()) {
            return new ArrayList<>(); // Se il file non esiste, la casella è vuota
        }

        try {
            // Jackson legge il file e lo trasforma in una Lista di Oggetti Email in automatico!
            return JsonSerializer.getObjectMapper().readValue(userFile, new TypeReference<List<Email>>() {});
        } catch (IOException e) {
            HelloController.getInstance().logMessage("Errore lettura file per " + userEmail + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Salva l'intera casella di posta di un utente.
     */
    private synchronized void saveUserEmails(String userEmail, List<Email> emails) {
        File userFile = new File(STORAGE_DIR + userEmail + ".json");
        try {
            // Jackson prende la lista e la scrive nel file in formato JSON
            JsonSerializer.getObjectMapper().writeValue(userFile, emails);
        } catch (IOException e) {
            HelloController.getInstance().logMessage("Errore salvataggio file per " + userEmail + ": " + e.getMessage());
        }
    }

    /**
     * Quando un utente INVIA una mail, questo metodo la aggiunge al file del DESTINATARIO.
     */
    public synchronized boolean addEmailToInbox(String recipient, Email newEmail) {
        List<Email> inbox = loadUserEmails(recipient);
        inbox.add(newEmail);
        saveUserEmails(recipient, inbox);
        HelloController.getInstance().logMessage("Salvata nuova mail in arrivo per: " + recipient);
        return true;
    }

    /**
     * Quando un utente ELIMINA una mail, questo metodo la rimuove dal suo file.
     */
    public synchronized boolean deleteEmail(String userEmail, String emailId) {
        List<Email> inbox = loadUserEmails(userEmail);

        // Cerca l'email con quell'ID e la rimuove
        boolean removed = inbox.removeIf(email -> email.getId().equals(emailId));

        if (removed) {
            saveUserEmails(userEmail, inbox);
            HelloController.getInstance().logMessage("Mail eliminata per: " + userEmail);
        }
        return removed;
    }
}