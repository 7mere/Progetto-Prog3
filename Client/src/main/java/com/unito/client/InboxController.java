package com.unito.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.unito.client.models.EmailClient;
import java.util.List;
import com.unito.client.service.MailService;

public class InboxController {

    private MailService mailService = new MailService();
    private String currentUserEmail; // Da valorizzare nel metodo initUser()

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

    // Campo di ricerca (mittente / oggetto)
    @FXML
    private TextField searchField;

    // Tabella della inbox tipizzata con EmailClient
    @FXML
    private TableView<EmailClient> messageTable;

    // Colonne della tabella (Assicurati che in SceneBuilder o nell'FXML abbiano questi fx:id!)
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
        // 1. Colleghiamo le colonne della grafica alle Property della classe EmailClient
        colFrom.setCellValueFactory(cellData -> cellData.getValue().senderProperty());
        colSubject.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        // 2. Colleghiamo la lista osservabile alla tabella
        messageTable.setItems(emailList);

        // 3. Creiamo dei DATI FINTI per testare la grafica
        EmailClient mail1 = new EmailClient("1", "professore@unito.it", "Voti Progetto", "Bravi, avete preso 30!", "12/05/2026");
        EmailClient mail2 = new EmailClient("2", "segreteria@unito.it", "Tasse", "Ricordati di pagare la rata.", "10/05/2026");
        EmailClient mail3 = new EmailClient("3", "mario.rossi@edu.unito.it", "Appunti Prog 3", "Ciao, mi passi gli appunti sui Socket?", "09/05/2026");

        emailList.addAll(mail1, mail2, mail3);

        // 4. (EXTRA) Quando clicchi su una mail, aggiorna i dettagli a destra!
        messageTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Aggiorna le label che hai già preparato nel controller
                detailFromLabel.setText(newValue.getSender());
                detailSubjectLabel.setText(newValue.getSubject());
                detailDateLabel.setText(newValue.getDate());
                detailBodyArea.setText(newValue.getBody());

                // SBLOCCA I BOTTONI!
                replyButton.setDisable(false);
                replyAllButton.setDisable(false);
                forwardButton.setDisable(false);
                deleteButton.setDisable(false);

                // Opzionale: stampa in console per farti vedere che funziona
                System.out.println("Selezionata email da: " + newValue.getSender());
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
        // 1. Prendi la mail attualmente selezionata nella tabella
        EmailClient selectedEmail = messageTable.getSelectionModel().getSelectedItem();

        if (selectedEmail != null) {
            // 2. Cambia tab
            rightTabPane.getSelectionModel().select(1);

            // 3. Precompila i campi
            composeModeLabel.setText("Modalità: REPLY");
            toField.setText(selectedEmail.getSender()); // Rispondo al mittente!
            subjectField.setText("Re: " + selectedEmail.getSubject());

            // 4. Inserisco il messaggio originale sotto
            bodyArea.setText("\n\n--- Messaggio Originale ---\n" + selectedEmail.getBody());
            bodyArea.positionCaret(0); // Mette il cursore del mouse all'inizio per scrivere
        }
    }

    @FXML
    private void onDeleteClick() {
        EmailClient selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Rimuove la mail dalla lista osservabile (la tabella si aggiornerà da sola!)
            emailList.remove(selected);

            // Svuota i dettagli a destra
            detailFromLabel.setText("-");
            detailSubjectLabel.setText("-");
            detailDateLabel.setText("-");
            detailBodyArea.clear();

            // Disabilita di nuovo i bottoni perché non c'è più nulla di selezionato
            deleteButton.setDisable(true);
            replyButton.setDisable(true);
            replyAllButton.setDisable(true);
            forwardButton.setDisable(true);
        }
    }

    @FXML
    private void onSendClick() {

        // 1. Controllo validità sintattica prima di fare qualsiasi cosa
        String toAddress = toField.getText();

        if (!areValidEmails(toAddress)) {
            // Mostriamo l'errore nell'apposita label rossa che avevate preparato
            addressValidationLabel.setText("Errore: uno o più indirizzi di destinazione non sono validi!");
            addressValidationLabel.setVisible(true);
            addressValidationLabel.setManaged(true);
            return; // Interrompe il metodo, così non invia nulla!
        }

        // Se va tutto bene, nascondiamo l'errore (se era visibile da prima)
        addressValidationLabel.setVisible(false);
        addressValidationLabel.setManaged(false);

        // 1. Disabilita il bottone invia e mostra lo spinner
        sendButton.setDisable(true);
        busyIndicator.setVisible(true);
        busyIndicator.setManaged(true);
        statusBarLabel.setText("Invio messaggio in corso...");

        // 2. Crea un Task per simulare il lavoro in background senza bloccare la grafica
        javafx.concurrent.Task<Void> sendTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1500); // Finge di metterci 1.5 secondi per inviare al server
                return null;
            }
        };

        // 3. Cosa fare quando ha finito
        sendTask.setOnSucceeded(e -> {
            sendButton.setDisable(false);
            busyIndicator.setVisible(false);
            busyIndicator.setManaged(false);
            statusBarLabel.setText("Messaggio inviato con successo!");

            // Pulisce e torna ai dettagli
            onClearComposeClick();
            rightTabPane.getSelectionModel().select(0);
        });

        // 4. Avvia il thread
        new Thread(sendTask).start();
    }

    @FXML
    private void onForwardClick() {
        EmailClient selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rightTabPane.getSelectionModel().select(1);
            composeModeLabel.setText("Modalità: FORWARD");

            toField.clear(); // Il destinatario è vuoto perché lo sto inoltrando a un amico
            subjectField.setText("Fwd: " + selected.getSubject());

            bodyArea.setText("\n\n--- Messaggio Inoltrato ---\nDa: " + selected.getSender() + "\nData: " + selected.getDate() + "\n\n" + selected.getBody());
            bodyArea.positionCaret(0);
        }
    }

    @FXML
    private void onReplyAllClick() {
        EmailClient selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rightTabPane.getSelectionModel().select(1);
            composeModeLabel.setText("Modalità: REPLY-ALL");

            // Per ora mettiamo il mittente originale.
            // In futuro qui aggiungeremo anche gli altri destinatari leggendo selected.getRecipients()
            toField.setText(selected.getSender());
            subjectField.setText("Re: " + selected.getSubject());

            bodyArea.setText("\n\n--- Messaggio Originale ---\n" + selected.getBody());
            bodyArea.positionCaret(0);
        }
    }

    // ... i tuoi metodi precedenti (es. onSendClick, onReplyClick...)

    // INCOLLA DA QUI:
    public void initUser(String email) {
        this.currentUserEmail = email;
        userEmailLabel.setText(email);

        // Avvio il Thread che controlla le mail ogni 5 secondi
        startPollingThread();
    }

    private void startPollingThread() {
        Thread pollingThread = new Thread(() -> {
            while (true) {
                try {
                    // Aspetta 5 secondi
                    Thread.sleep(5000);

                    // Chiede al server
                    List<EmailClient> nuoveMail = mailService.fetchNewEmails(currentUserEmail);

                    if (!nuoveMail.isEmpty()) {
                        javafx.application.Platform.runLater(() -> {
                            emailList.addAll(nuoveMail);
                            lastUpdateLabel.setText(java.time.LocalTime.now().toString());
                            notificationBar.setVisible(true);
                            notificationBar.setManaged(true);
                        });
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        connectionDot.setFill(javafx.scene.paint.Color.RED);
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
            if (!com.unito.client.LoginController.isValidEmail(part.trim())) {
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
}
