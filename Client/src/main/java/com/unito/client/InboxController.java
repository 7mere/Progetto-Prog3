package com.unito.client;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.unito.client.models.EmailClient;
import com.unito.client.service.MailService;
import com.unito.shared.utils.JsonSerializer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class InboxController {

    private final MailService mailService = new MailService();
    
    private String currentUserEmail; // Da valorizzare nel metodo initUser()
    
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Lock fileLock = new ReentrantLock();
    
    /* =========================================================
     * SEZIONE UTENTE / STATO CONNESSIONE
     * ---------------------------------------------------------
     * - Mostra l’utente loggato
     * - Mostra stato connessione (pallino + testo)
     * ========================================================= */

    // Email dell’utente loggato (arriva dalla schermata di login)
    @FXML
    private Label userEmailLabel;

    // Pallino colorato che indica lo stato (es. verde=connesso, rosso=non connesso)
    @FXML
    private Circle connectionDot;

    // Testo che indica lo stato ("CONNESSO" / "NON CONNESSO")
    @FXML
    private Label connectionStatusLabel;


    /* =========================================================
     * SEZIONE NOTIFICHE / ERRORI
     * ---------------------------------------------------------
     * - Barra di notifica (es. "Nuovo messaggio ricevuto")
     * - Messaggi di errore (trasparenza verso l’utente)
     * ========================================================= */

    // Barra di notifica (contenitore) — di solito visibile/hidden a runtime
    @FXML
    private HBox notificationBar;

    // Testo della notifica
    @FXML
    private Label notificationLabel;

    // Bottone per chiudere la notifica
    @FXML
    private Button dismissNotificationButton;

    // Etichetta per errori (connessione, validazione, ecc.)
    @FXML
    private Label errorLabel;


    /* =========================================================
     * SEZIONE INBOX (LISTA MESSAGGI)
     * ---------------------------------------------------------
     * - Ricerca
     * - Tabella con i messaggi
     * - Azioni sul messaggio selezionato (delete/reply/...)
     * ========================================================= */

    // Timestamp/label dell’ultimo aggiornamento inbox
    @FXML
    private Label lastUpdateLabel;

    // Tabella della inbox tipizzata con EmailClient
    @FXML
    private TableView<EmailClient> messageTable;

    // Colonne della tabella
    @FXML
    private TableColumn<EmailClient, String> colFrom;

    @FXML
    private TableColumn<EmailClient, String> colSubject;

    @FXML
    private TableColumn<EmailClient, String> colDate;

    // La lista "Osservabile" che conterrà i nostri dati
    private ObservableList<EmailClient> emailList = FXCollections.observableArrayList();

    // Azioni rapide sul messaggio selezionato
    @FXML
    private Button deleteButton;

    @FXML
    private Button replyButton;

    @FXML
    private Button replyAllButton;

    @FXML
    private Button forwardButton;


    /* =========================================================
     * SEZIONE DESTRA (DETTAGLI + COMPOSER)
     * ---------------------------------------------------------
     * - TabPane con "Dettagli" e "Scrivi"
     * - Dettagli del messaggio selezionato
     * - Composer per nuovo / reply / forward
     * ========================================================= */

    // Contenitore tab (Dettagli / Scrivi)
    @FXML
    private TabPane rightTabPane;

    /* ---------- Dettagli messaggio selezionato ---------- */

    @FXML
    private Label detailFromLabel;

    @FXML
    private Label detailToLabel;

    @FXML
    private Label detailDateLabel;

    @FXML
    private Label detailSubjectLabel;

    @FXML
    private TextArea detailBodyArea;

    // Bottone "Nuovo messaggio" (tipicamente porta al tab "Scrivi")
    @FXML
    private Button composeNewButton;


    /* ---------- Composer (Scrivi) ---------- */

    // Label che indica la modalità: NUOVO / REPLY / REPLY-ALL / FORWARD
    @FXML
    private Label composeModeLabel;

    @FXML
    private TextField toField;

    @FXML
    private TextField ccField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea bodyArea;

    // Label per errori di validazione indirizzi (To/Cc)
    @FXML
    private Label addressValidationLabel;

    @FXML
    private Button clearComposeButton;

    @FXML
    private Button sendButton;


    /* =========================================================
     * SEZIONE BARRA IN BASSO (STATO OPERAZIONI)
     * ---------------------------------------------------------
     * - Messaggio di stato ("Pronto", "Caricamento...", ecc.)
     * - Indicatore di lavoro (spinner)
     * ========================================================= */

    @FXML
    private Label statusBarLabel;

    // Spinner mostrato durante operazioni lunghe (rete/polling)
    @FXML
    private ProgressIndicator busyIndicator;

    @FXML
    public void initialize() {
        // Binding delle colonne della TableView con le Properties del modello EmailClient
        colFrom.setCellValueFactory(cellData -> cellData.getValue().senderProperty());
        colSubject.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        messageTable.setItems(emailList);

        // Listener per l'aggiornamento dinamico del pannello di lettura
        messageTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                detailFromLabel.setText(newValue.getSender());
                detailSubjectLabel.setText(newValue.getSubject());
                detailDateLabel.setText(newValue.getDate());
                detailBodyArea.setText(newValue.getBody());

                // Abilitazione dei comandi contestuali
                replyButton.setDisable(false);
                replyAllButton.setDisable(false);
                forwardButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    @FXML
    private void onComposeNewClick() {
        // 1. Cambia il tab passando da "Dettagli" (0) a "Scrivi" (1)
        rightTabPane.getSelectionModel().select(1);

        // 2. Imposta la modalità
        composeModeLabel.setText("Modalità: NUOVO");

        // 3. Svuota tutti i campi
        toField.clear();
        ccField.clear();
        subjectField.clear();
        bodyArea.clear();
    }

    @FXML
    private void onClearComposeClick() {
        // Fa la stessa cosa: svuota i campi ma restando nel tab "Scrivi"
        toField.clear();
        ccField.clear();
        subjectField.clear();
        bodyArea.clear();
    }

    @FXML
    private void onReplyClick() {
        EmailClient selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Cambia tab e imposta la label in alto
        rightTabPane.getSelectionModel().select(1);
        composeModeLabel.setText("Modalità: REPLY");

        // Compila i campi
        toField.setText(selected.getSender());

        String subject = selected.getSubject() != null ? selected.getSubject() : "";
        subjectField.setText(subject.startsWith("Re:") ? subject : "Re: " + subject);

        // Prepara il testo citando il messaggio originale
        bodyArea.setText("\n\n--- Messaggio Originale ---\nDa: " + selected.getSender() + "\nData: " + selected.getDate() + "\n\n" + selected.getBody());
        bodyArea.positionCaret(0); // Mette il cursore pronto in alto per scrivere
    }

    @FXML
    private void onDeleteClick() {
        EmailClient selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected == null) return; // Se non c'è nulla di selezionato, non facciamo niente

        /*
        // Usiamo un Task per non "congelare" la grafica mentre la rete lavora
        javafx.concurrent.Task<Boolean> deleteTask = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return mailService.deleteEmail(currentUserEmail, selected.getId());
            }
        };

        deleteTask.setOnSucceeded(e -> {
            if (deleteTask.getValue()) {
                emailList.remove(selected); // Rimuoviamo la mail dalla tabella visiva
                saveLocalInbox();
                messageTable.getSelectionModel().clearSelection();
                statusBarLabel.setText("Email eliminata correttamente.");
            }
        });

        // new Thread(deleteTask).start();
        */

       executor.submit(() -> {
        try {
            boolean success = mailService.deleteEmail(currentUserEmail, selected.getId());

            Platform.runLater(() -> {
                if (success) {
                    emailList.remove(selected);
                    saveLocalInbox(); // aggiorna la inbox 
                    messageTable.getSelectionModel().clearSelection();

                    // permette di resettare la label del email eliminata
                    detailFromLabel.setText("");
                    detailSubjectLabel.setText("");
                    detailDateLabel.setText("");
                    detailBodyArea.setText("");

                    statusBarLabel.setText("Email eliminata correttamente.");
                } else {
                    statusBarLabel.setText("Errore eliminazione.");
                }
            });

        } catch (Exception e) {
            Platform.runLater(() ->
                statusBarLabel.setText("Errore di rete.")
            );
        }
        });
    }

    @FXML
    private void onSendClick() {
        String toAddress = toField.getText();

        if (!areValidEmails(toAddress)) {
            addressValidationLabel.setText("Errore: indirizzi di destinazione non validi!");
            addressValidationLabel.setVisible(true);
            addressValidationLabel.setManaged(true);
            return;
        }

        addressValidationLabel.setVisible(false);
        addressValidationLabel.setManaged(false);

        sendButton.setDisable(true);
        busyIndicator.setVisible(true);
        busyIndicator.setManaged(true);
        statusBarLabel.setText("Invio messaggio in corso...");

        // 1. Creiamo la mail con i dati presi dalla grafica
        EmailClient emailDaInviare = new EmailClient();
        // Genera un id univoco
        emailDaInviare.setId(UUID.randomUUID().toString());
        // setta la data odierna per la mail da inviare
        emailDaInviare.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        emailDaInviare.setSender(currentUserEmail);
        emailDaInviare.setSubject(subjectField.getText() != null ? subjectField.getText() : "");
        emailDaInviare.setBody(bodyArea.getText() != null ? bodyArea.getText() : "");

        List<String> destinatari = new ArrayList<>();
        destinatari.add(toAddress.trim());

        // Se c'è qualcosa nel campo opzionale Cc, lo aggiungiamo
        if (ccField.getText() != null && !ccField.getText().trim().isEmpty()) {
            for (String addr : ccField.getText().split(",")) {
                destinatari.add(addr.trim());
            }
        }
        emailDaInviare.setRecipients(destinatari);

        // Task asincrono per delegare l'operazione di rete fuori dal JavaFX Application Thread
        Task<Void> sendTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean success = mailService.sendEmail(emailDaInviare);
                if (!success) {
                    throw new Exception("Operazione rifiutata dal server.");
                }
                return null;
            }
        };

        // Gestione dei callback di successo o fallimento
        sendTask.setOnSucceeded(e -> {
            sendButton.setDisable(false);
            busyIndicator.setVisible(false);
            busyIndicator.setManaged(false);
            statusBarLabel.setText("Messaggio inviato con successo!");

            onClearComposeClick();
            rightTabPane.getSelectionModel().select(0);
        });

        // 4. Cosa fare se il server è irraggiungibile o rifiuta
        sendTask.setOnFailed(e -> {
            sendButton.setDisable(false);
            busyIndicator.setVisible(false);
            busyIndicator.setManaged(false);
            statusBarLabel.setText("Errore nell'invio del messaggio!");

            addressValidationLabel.setText("Errore di rete durante l'invio.");
            addressValidationLabel.setVisible(true);
            addressValidationLabel.setManaged(true);
        });

        new Thread(sendTask).start();
    }

    @FXML
    private void onForwardClick() {
        EmailClient selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        rightTabPane.getSelectionModel().select(1);
        composeModeLabel.setText("Modalità: FORWARD");

        // Lasciamo il destinatario vuoto per permetterti di inserirlo
        toField.clear();

        String subject = selected.getSubject() != null ? selected.getSubject() : "";
        subjectField.setText(subject.startsWith("Fwd:") ? subject : "Fwd: " + subject);

        bodyArea.setText("\n\n--- Messaggio Inoltrato ---\nDa: " + selected.getSender() + "\nData: " + selected.getDate() + "\n\n" + selected.getBody());
        bodyArea.positionCaret(0);
    }

    @FXML
    private void onReplyAllClick() {
        EmailClient selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        rightTabPane.getSelectionModel().select(1);
        composeModeLabel.setText("Modalità: REPLY-ALL");

        // Creiamo la lista di tutti quelli a cui rispondere
        List<String> tuttiDestinatari = new ArrayList<>();
        tuttiDestinatari.add(selected.getSender()); // Aggiungiamo il mittente

        // Aggiungiamo gli altri destinatari originali (se ci sono e se non siamo noi)
        if (selected.getRecipients() != null) {
            for (String rec : selected.getRecipients()) {
                if (!rec.equals(currentUserEmail) && !tuttiDestinatari.contains(rec)) {
                    tuttiDestinatari.add(rec);
                }
            }
        }

        toField.setText(String.join(", ", tuttiDestinatari));

        String subject = selected.getSubject() != null ? selected.getSubject() : "";
        subjectField.setText(subject.startsWith("Re:") ? subject : "Re: " + subject);

        bodyArea.setText("\n\n--- Messaggio Originale ---\nDa: " + selected.getSender() + "\nData: " + selected.getDate() + "\n\n" + selected.getBody());
        bodyArea.positionCaret(0);
    }

    public void initUser(String email) {
        this.currentUserEmail = email;
        userEmailLabel.setText(email);

        loadLocalInbox();
        // Avvio il Thread che controlla le mail ogni 5 secondi
        startPollingThread();
    }

    private void startPollingThread() {
        Thread pollingThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    List<EmailClient> nuoveMail = mailService.fetchNewEmails(currentUserEmail);

                    // Platform.runLater sincronizza gli aggiornamenti grafici sul thread principale
                    Platform.runLater(() -> {
                        connectionDot.setFill(Color.GREEN);
                        connectionStatusLabel.setText("CONNESSO");
                        errorLabel.setVisible(false);
                        errorLabel.setManaged(false);

                        boolean aggiunteNuove = false;

                        // Verifica ed evita l'inserimento di messaggi duplicati in memoria
                        for (EmailClient serverMail : nuoveMail) {
                            boolean giaPresente = false;
                            for (EmailClient localMail : emailList) {
                                if (localMail.getId() != null && localMail.getId().equals(serverMail.getId())) {
                                    giaPresente = true;
                                    break;
                                }
                            }

                            if (!giaPresente) {
                                emailList.add(0, serverMail);
                                aggiunteNuove = true;
                            }
                        }

                        // Aggiornamento dello stato dell'UI in caso di nuovi payload
                        if (aggiunteNuove) {
                            saveLocalInbox();
                            lastUpdateLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                            notificationBar.setVisible(true);
                            notificationBar.setManaged(true);
                        }
                    });
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {

                    // Gestione visiva della caduta di connessione
                    Platform.runLater(() -> {
                        connectionDot.setFill(Color.RED);
                        connectionStatusLabel.setText("DISCONNESSO");
                        errorLabel.setText("Errore di connessione col server.");
                        errorLabel.setVisible(true);
                        errorLabel.setManaged(true);
                    });
                }
            }
        });

        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    // Metodo di supporto per validare più email separate da virgola
    private boolean areValidEmails(String emails) {
        if (emails == null || emails.trim().isEmpty()) return false;

        String[] parts = emails.split(",");
        for (String part : parts) {
            if (!LoginController.isValidEmail(part.trim())) {
                return false; // Se anche solo una è sbagliata, blocca tutto!
            }
        }
        return true;
    }

    @FXML
    private void onDismissNotificationClick() {
        notificationBar.setVisible(false);
        notificationBar.setManaged(false);
    }

    /**
     * Aggiorna la lista delle email corrente in un file JSON locale al client.
     */
    private void saveLocalInbox() {
        fileLock.lock();
        try {
            File dir = new File("client_data");
            if (!dir.exists()) dir.mkdir();

            File file = new File(dir, currentUserEmail + "_inbox.json");
            String json = JsonSerializer.serialize(new ArrayList<>(emailList));
            Files.writeString(file.toPath(), json);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio locale: " + e.getMessage());
        }
        finally {
            fileLock.unlock();
        }
    }

    /**
     * Carica le email salvate precedentemente sul disco locale.
     */
    private void loadLocalInbox() {
        try {
            File file = new File("client_data/" + currentUserEmail + "_inbox.json");
            if (file.exists()) {
                String json = Files.readString(file.toPath());
                EmailClient[] salvate = JsonSerializer.deserialize(json, EmailClient[].class);
                emailList.setAll(salvate);
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento locale: " + e.getMessage());
        }
    }
}
