# Progetto di laboratorio di Programmazione III

MAIL CLIENT / MAIL SERVER

Il Progetto di laboratorio consiste, complessivamente di due applicazioni (progetti IntelliJ IDEA diversi) distinte:

1) Un mail server che gestisce le caselle di posta elettronica degli utenti registrati sul server;

2) Un mail client che ciascun utente può eseguire per leggere la propria posta elettronica, inviare email ad altri account di posta elettronica (e/o a se stesso), etc.

Il mail client e il mail server sono entrambi implementati come applicazioni javaFXML basate sul pattern MVC. Ciascuno di essi è implementato in un progetto javaFX distinto e organizzato in package per modularità.
All'interno di queste applicazioni non deve esserci comunicazione diretta tra viste e model: ogni comunicazione tra questi due livelli deve essere mediata dal controller o supportata dal pattern Observer Observable.
Non si usino le classi deprecate Observer.java e Observable.java. Si usino le classi di JavaFX che supportano il pattern Observer Observable (properties, ObservableLists, etc).
Le applicazioni client e server devono essere eseguite in Java Virtual Machine distinte e comunicare solo ed esclusivamente attraverso la trasmissione di dati testuali in socket Java.
Il mail client e il mail server devono parallelizzare le attività che non necessitano di esecuzione sequenziale e gestire gli eventuali problemi di accesso a risorse in mutua esclusione. Si raccomanda di prestare molta attenzione alla progettazione per facilitare il parallelismo nell’esecuzione delle istruzioni.
Per la demo si assuma di avere 3 utenti di posta elettronica che comunicano tra loro. Si progetti però il sistema in modo da renderlo scalabile a molti utenti e a mailbox di grandi dimensioni.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

MAIL CLIENT - SPECIFICHE:

Quando l'utente lancia il mail client, il client chiede di inserire l'indirizzo di posta elettronica e utilizza questo come identificatore dell'utente durante l'esecuzione.
Il mail client mantiene i seguenti dati durante la propria esecuzione:
indirizzo di posta elettronica dell'utente;
casella postale dell'utente (inbox): lista dei messaggi di posta elettronica ricevuti dall'utente e non cancellati. Non gestite il cestino e neppure la outbox.
Il mail client è ignaro di quali siano gli utenti registrati sul server. Quando l'utente inserisce un indirizzo di posta elettronica, ne verifica la correttezza sintattica (ben formatezza) utilizzando le espressioni regolari (Regex), chiedendo all'utente di reinserirlo se sintatticamente errato.
Per verificare se un indirizzo di posta (ben formato) è esistente, il mail client si connette al server che restituisce risposta positiva o negativa.
Si assuma che una stessa persona usi sempre lo stesso device per leggere la mail (no device multipli).


GUI (Graphical User Interface):

La GUI (view di FXML) gestisce SOLO la INBOX e permette all'utente di:
inserire il proprio indirizzo di posta elettronica come unica forma di autenticazione (non è prevista l'iscrizione di un nuovo utente);
visualizzare la lista dei messaggi in entrata e scorrerla per selezionare il messaggio da visualizzare in dettaglio;
visualizzare i dettagli di uno specifico messaggio di posta elettronica;
cancellare un messaggio di posta elettronica dalla inbox;
creare un messaggio di posta elettronica specificando uno o più destinatari; inviare il messaggio al server tramite opportuno bottone;
rispondere a un messaggio di posta elettronica presente nella inbox in REPLY (solo al mittente) o in REPLY-ALL (a tutti i destinatari del messaggio, incluso il mittente);
inoltrare un messaggio (forward) a uno o più destinatari;
per l'invio di messaggi e il forward, il client deve verificare la correttezza sintattica degli indirizzi di posta elettronica inseriti e permettere di reinserire i dati se sintatticamente errati; il controllo di esistenza dei destinatari deve invece essere effettuato dal server;
visualizzare lo stato della connessione con il server (connesso/non connesso).
L’interfaccia utente deve essere:

parzialmente responsive:  la GUI dovrà mostrare automaticamente la lista dei messaggi aggiornata, senza che l'utente debba compiere azioni specifiche per fare il refresh. Inoltre, all'arrivo di un nuovo messaggio dovrà notificare l'utente. Non si richiede che la GUI si adatti a schermi di dimensioni differenti.
comprensibile (trasparenza). Inoltre, a fronte di errori, deve segnalare il problema all’utente.
funzionale (efficacia) per permettere di eseguire operazioni limitando il numero di click da fare.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
MAIL SERVER:
Il mail server gestisce una lista di caselle di posta elettronica e ne mantiene la persistenza utilizzando file (txt o binari, JSON se sapete come gestirli, a vostra scelta - non si possono usare database) per memorizzare i messaggi in modo permanente.
Ogni casella di posta elettronica contiene:

Nome dell’account di mail associato alla casella postale (es., giorgio@mia.mail.com).
Lista (eventualmente vuota) di messaggi. I messaggi di posta elettronica sono istanze di una classe Email che specifica ID, mittente, destinatario/i, argomento, testo e data di spedizione del messaggio.
Il mail server ha un’interfaccia grafica sulla quale viene visualizzato il log degli eventi che occorrono durante l’interazione tra i client e il server.

Per esempio: apertura/chiusura di una connessione tra mail client e server, invio di messaggi da parte di un client a uno o più destinatari, errori nella consegna di messaggi ai destinatari.
NB: NON fare log di eventi locali al client come il fatto che l'utente ha schiacciato un bottone, aperto una finestra o simili in quanto non sono di pertinenza del server.
NB: si assuma che il server abbia un numero fisso di account di posta elettronica, precompilato (per es. 3 account). Non si richiede che da mail client si possano registrare nuovi account di posta sul server.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

COMUNICAZIONE TRA CLIENT E SERVER:

La verifica dell'esistenza degli indirizzi di posta elettronica è responsabilità del server. In caso l'utente inserisca indirizzi di posta elettronica non esistenti, il server deve inviare messaggio di errore al client. Per esempio, in merito al fallimento di un'autenticazione, oppure in caso l'utente tenti di inviare un messaggio a un account inesistente.

Il mail client non deve andare in crash se il mail server viene spento. Gestire i problemi di connessione al mail server inviando opportuni messaggi di errore all’utente e fare in modo che il mail client si riconnetta automaticamente al server quando questo è novamente attivo.



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ULTERIORI REQUISITI (MAIL CLIENT E MAIL SERVER)

Non gestite socket permanenti per collegare client e server: fate in modo che, come HTTP, il client chieda di aprire la connessione ogni volta che ha bisogno di fare un'operazione. 
Non trasferite intere caselle di posta elettronica da client a server, o viceversa, per questioni di scalabilità del servizio. Quando il client chiede aggiornamenti al server, il server deve solo inviare i messaggi che non sono stati precedentemente distribuiti al client.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

PROMEMORIA PER L'ESAME
Il progetto SW può essere svolto in gruppo (max 3 persone) o individualmente. Se lo si svolge in gruppo la discussione deve essere fatta dall’intero gruppo in soluzione unica.
La discussione potrà essere fatta nelle date di appello orale dell’insegnamento (appelli su Esse3 nominati "Discussione di laboratorio"). 
Si può discutere il progetto SW prima o dopo aver sostenuto la prova scritta, a propria scelta. 
Come da regolamento d’esame il voto finale si ottiene come media del voto della prova teorica (scritta) e della discussione di laboratorio (i due voti hanno ugual peso nella media). 
Il voto finale deve essere registrato entro fine settembre 2025, data oltre la quale non è possibile mantenere i voti parziali. Leggere il regolamento d’esame sulla pagina web dell’insegnamento per ulteriori dettagli.


## Luca Gado

### Struttura del progetto

Si è deciso di creare un solo progetto Gradle multi-module che conterrà due moduli FXML separati (uno per il client e l'altro per il server), in questo modo si creeranno due JVM e quindi due build.gradle ben distinte 

Si gestisce una compilazione automatica dei file Java usando Maven 

### N.B. con Gradle

- gradlew e gradlew.bat: sono file eseguibili chiamati Gradle Wrapper, sono script che permettono di eseguire il tool di automazione della build Gradle senza doverlo installare e garantendo una versione coerente per tutti gli sviluppatori. gradlew è per linux/macOS, mentre gradlew.bat è per Windows

- gradle.properties: file di testo che contiene impostazioni globali; definisce variabili come la memoria massima per la compilazione o versioni comuni di librerie.

- settings.gradle: file che definisce l'architettura del progetto ed è il punto di ingresso. 
Elenca quali moduli o sotto-progetti devono essere inclusi nella build.
Gradle legge prima settings.gradle per capire quanti e quali file build.gradle deve cercare ed eseguire

- build.gradle: è un file di configurazione per Gradle, che permette l'automazione della compilazione per gestione dei progetti Java, definendo dipendenze, task e configurazione del progetto in modo efficiente usando un linguaggio DSL (Domain Specific Language)

- la cartella gradle/: serve a garantire che chiunque scarichi il progetto utilizzi la stessa identica versione di Gradle. Contiene principalmente la sottocartella wrapper/ con due file fondamentali: gradle-wrapper.jar (codice eseguibile che permette al "wrapper" di funzionare e scaricare automaticamente Gradle se non è presente sul computer) e gradle-wrapper.properties (Un file di testo che specifica quale versione di Gradle scaricare (es. la 8.5) e da quale indirizzo)

- la cartella .gradle/: viene generata automaticamente da Gradle durante la compilazione. Non deve essere condivisa su Git poiché contiene dati specifici del tuo PC.

- la cartella build/: sono i risultati dei processi; contiene tutto ciò che Gradle genera durante la compilazione: file .class, file temporanei e l'eseguibile finale.
Le istruzioni scritte in build.gradle dicono a Gradle cosa mettere in questa cartella. Quando esegui ./gradlew clean, la cartella build/ viene eliminata per garantire una nuova compilazione da zero. 

### N.B. con Maven

- si usa il comando ./mvnw javafx:run per far eseguire il progetto da terminale

- pom.xml: Si ha che Maven richiede Java 21 dalla parte del Server

### Persistenza dei file nel server
Come fare? Meglio txt o binari? Oppure JSON

### Protocollo di comunicazione tra client e server

#### Protocollo di comunicazione client->server

Il client invia vari comandi al server dove ognuno di questi definisce un tipo di operazione richiesta di controllo:
- SEND: invio di messaggio, il client manda il comando SEND seguito 

- CHECK <email> — verifica esistenza account.

- risposta: OK o ERR:account-not-found

- AUTH <email> — usato all'avvio per autenticare/identificare (server risponde OK/ERR)

- FETCH_NEW <email> <lastKnownMessageId> — chiede solo messaggi non ancora inviati al client.

    - risposta: MSG_LIST <n> seguito da n messaggi, ognuno con LENGTH:<bytes> + JSON

    - oppure MSG_LIST 0

- SEND — invio di messaggio: il client manda SEND poi LENGTH:<bytes> e poi JSON dell'Email. Server risponde per ogni destinatario DELIVERED <recipient> o FAILED <recipient> reason

- DELETE <email> <messageId> — cancella messaggio dalla inbox server-side (o marca come cancellato)

- PING — testa connessione

- QUIT — chiude connessione


Meglio usare una classe di richiesta e risposta che verrà messo in una libreria comune sia per il client che per il server? 
Si usa una libreria comune tra client e server in modo che contenga tutto ciò che c'è di uguale tra i due e garantisce la stessa versione del protocollo, come i tipi di messaggio, comandi, classi che sono serializzate in JSON, costanti di protocollo??, degli helper per (de)serializzare il JSON 

Libreria è una cartella chiamata shared che rappresenta il contratto tra client e server 
Contiene:
- il `models/` classi di dominio che vengono serializzate in JSON per la comunicazione client-server. In questo caso `Email.java`
- il `protocol/` definisce struttura e regole di comunicazione tra client e server. Come `CommandOperation.java` `ProtocolConstants.java` `Message.java`
- l'`utils/` helper per operazioni comuni tra client e server. Cioè il `JsonSerializer.java`

#### Classe Email 

Ho evitato Serializable perché la specifica richiede la trasmissione di dati testuali via socket. Inoltre Serializable introduce un forte accoppiamento tra client e server e rende il protocollo fragile ai cambiamenti. Ho preferito un protocollo testuale (JSON) che è più scalabile, debuggabile e aderente alle richieste; inoltre JSON resta preferibile (leggibile, ispezionabile) ma Serializable è accettabile per file binari.

Solo il MAIL CLIENT crea nuove istanze di Email, quando l’utente:
 - crea un nuovo messaggio
 - risponde (reply / reply-all)
 - inoltra (forward)

Email ha il costruttore vuoto perché serve a Jackson (JSON): Jackson crea l’oggetto vuoto poi chiama i setter
Senza costruttore vuoto: deserializzazione fallisce

Ciclo di vita completo di una Email (passo-passo):
1) Creazione (CLIENT); con istanza della classe Email
2) Invio al server; client serializza l'oggetto Email in JSON e lo manda via socket
3) Ricezione sul SERVER; riceve il JSON e lo ricostruisce come oggetto (deserializzazione)
4) Salvataggio; prende l’istanza Email e la aggiunge alla mailbox dei destinatari (vive nel file JSON e in memoria del server)
5) Distribuzione al client destinatario; riceve JSON e ricostruisce l’oggetto Email, questa è una nuova istanza Java, ma rappresenta lo stesso messaggio (stesso id) 
6) Visualizzazione; inserisce l’Email nella ObservableList<Email> inbox e a ListView la mostra automaticamente

Per un progetto reale sarebbe meglio fare una configurazione della porta del server all'esterno per garantire flessibilità e separazione tra codice e configurazione; altrimenti se la si scrive nel codice bisognerebbe ricompilare e ridistribuire rischiando cosi errori inutili; altrimenti si scrive direttamente sul codice
Uso della seguente fonte:
 - https://stackoverflow.com/questions/24093257/thread-currentthread-getcontextclassloader-getresourceasstream-reads-a-prope

Server aperto in backend nel metodo main() della classe ServerMain 

#### Serialiazzione e Deserializzazione JSON
Uso di una libreria di mapping JSON per serializzare o deserializzare l'oggetto Email, in questo caso Jackson, che permetterà di trasformare l'oggetto in JSON o viceversa
Aggiungendo una dipendenza all'interno di Maven

#### Uso di Jackson
Libreria Java per serializzare da oggetti Java a JSON e viceversa (deserializzare)
Dipendenza Maven con Jackson
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jsr310</artifactId>
    </dependency>

Uso della classe centrale: ObjectMapper mapper = new ObjectMapper();


### Thread e Thread Pool
Si ha un solo thread che rimane in attessa di connessioni (accpet()) e li delega al pool di thread
Si è aggiunto un pool di thread per gestire in maniera sincronizzata e parallela i task invitati per gestire la connessione fra il server e il client che l'ha richiesto
L'ha decisione ricade tra l'executors newFixedThreadPool (che è gia preconfigurato) oppure ThreadPoolExecutor (che è configurabile, più adatto per un controllo su una architettura reale come con un server o un socket); per questo si è deciso di optare per un controllo completo (in modo da non creare una coda infinita ma piuttosto una controllata)
Altrimenti si potrebbe usare newCachedThreadPool() per creare un thread per ogni connessione, ma potrebbe essere inefficiente se ci sono molte connessioni

Nella classe ServerSocketManager si gestisce i socket del lato server, in cui:
1) Nel costruttore a parte l'inizializzazione degli attributi passati come parametri, si setta il Thread del ServerSocketManager come daemon per assicurare che il servizio non blocchi lo spegnimento (shutdown) della JVM, favorendo avvio/terminazione controllati; se l’unico thread rimasto è questo, la JVM può terminare.
2) Poi prima si esegue il metodo start(), chiamata dal thread della GUI con server.start(), per aggiungere la logica di inizializzazione del server come la creazione di un server socket sulla porta specializzata e un thread di Pool usando ThreadPoolExecutor
3) Successivamente si chiama il metodo run(), tramite il metodo super.start() chiamato in start(), per la logica di accettazzione delle connessioni a lato client, in cui si avvia il thread (daemon) del ServerSocketManager in parallelo al thread principale (GUI)
4) Nel metodo run() c'è un loop di accettazzione dei socket (accept()) e per ogni nuova connessione accettata il server sottomette un task (fornendo un'istanza dell'oggetto ClientHandler, che implementa Runnable) al pool, in cui questo pool provvede a creare/riutilizzare un thread worker per eseguire quel Runnable.
Inoltre nel ciclo principale c'è un timeout per poter controllare periodicamente Thread.interrupted() e sapere quando uscire
5) Infine quando il thread è stato interrotto (chiuso la GUI), allora si chiude il server socket e il thread pool

N.B. 
    il metodo start() che “avvia” il server non è mai invocato direttamente all’interno dello stesso ServerSocketManager; è sempre un altro thread (GUI o main) che lo richiama, mentre super.start() è quello che innesca la creazione del thread esecutore. Per il resto la logica di accettazione/dispatch all’interno di run() e il ruolo del pool sono esattamente come li hai spiegati.

### Consigli da parte del client
Usare Platform.runLater(() -> ...) per eseguire operazione di aggiornamento della UI da thread in background; permette di aggiornare l'interfaccia utente in modo reattivo


RIVEDERE LA CREAZIONE DEGLI UTENTI


CREARE LA LIBRERIA CONDIVISA PER ENTRAMBI E CONTROLLARE, LA PERSISTENZA JSON 
