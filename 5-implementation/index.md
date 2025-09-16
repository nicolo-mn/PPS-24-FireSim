# Implementazione
Questo capitolo illustra e giustifica le principali decisioni implementative.
Inoltre, all’interno del codice è inclusa la documentazione Scaladoc, utile a descrivere nel dettaglio ogni sua componente.

## Model
### Juri Guglielmi

### Riccardo Mazzi


### Nicolò Monaldini

## Controller
Come mostrato nelle figure UML, SimController espone pubblicamente solo due metodi dell'interfaccia Controller.

### `handleViewMessage`
Questo metodo permette alla View di inviare comandi al controller in modo disaccoppiato. La View crea un oggetto `ViewMessage` e lo passa al Controller, che ne invoca l’esecuzione tramite `msg.execute(this)`.In questo modo, la logica specifica del comando è delegata al messaggio stesso, anziché al controller.

Questo approccio utilizza il Command Pattern, che riduce l’accoppiamento tra View e Controller, migliora l’incapsulamento e rende più semplice aggiungere nuove operazioni senza modificare la View né la logica interna del controller.

`ViewMessage` è un sealed trait che rappresenta un comando inviato dalla View al Controller. Ogni sottoclasse incapsula sia i dati necessari per l’operazione sia la logica di esecuzione attraverso il metodo `execute(controller: SimController)`.

Grazie a questo meccanismo, la View non chiama direttamente i metodi del controller, ma si limita a creare e inviare un messaggio, lasciando al comando il compito di agire sul controller.

### *`loop`*
Questo metodo viene chiamato dal main thread e permette al controller di avviare il loop della simulazione.

Il controller salva i millisecondi da aspettare tra un tick e l'altro passati come argomento del metodo.
Tenendo traccia dei valori attuali e originali si potrà poi cambiare la velocità della simulazione a seconda della scelta dell'utente.

Sia il main thread che il thread della view accederanno ad alcune variabili condivise, principalmente flag booleani ma anche le dimensioni della mappa scelte dall'utente e i millisecondi della velocità di simulazione citati prima.
Queste variabili sono dichiarate con il tag `volatile` per un accesso thread safe.

Per sincronizzare i due thread e permettere al main thread di lasciare le risorse mentre la mappa non è stata ancora generata si usa una variabile `lock`.

Usando queste variabili thread safe, all'interno del loop il main thread potrà aspettare prima che la mappa sia generata,
usare il metodo `onTick` e quindi chiamare il model per avanzare nella simulazione (se questa sta runnando),
e infine chiamare il metodo `handleQueuedCells` per far inserire al model le celle piazzate dall'utente (dopo aver fatto avanzare il model, assicurando una corretta sincronizzazione).

## View
Per realizzare la GUI è stato utilizzato Scala Swing. L'interfaccia grafica è composta da un `Panel` che contiene la griglia con cui l'utente può interagire e da un `BoxPanel` contenente i vari controlli. 

L’utente può interagire con la griglia tramite click e trascinamenti, che consentono il posizionamento delle celle. Le coordinate grafiche ottenute dall’interazione vengono convertite in coordinate logiche attraverso il metodo pixelToCell, così da poter essere elaborate dal controller.

I controlli sono organizzati in righe attraverso l'uso di ulteriori `BoxPanel`. Poiché ogni riga ha caratteristiche in comune, come la spaziatura tra i vari controlli, un trait con self-types è stato utilizzato per decorare ogni `BoxPanel`. 

Le varie tipologie di celle sono identificate da un `enum` chiamato `CellViewType`. Le istanze di questa enumerazione sono ricevute dal controller per aggiornare la griglia, e sono utilizzate dalla view per associare un colore a ogni cella.

Uno `SplitPane` è utilizzato per dividere le due aree. La griglia è responsive, e reagisce al ridimensionamento della finestra e delle due aree. Questo è ottenuto tramite un metodo `gridGeometry` chiamato all'interno del metodo `paintComponent` del `Panel`, che utilizza la dimensione del `Panel` contenente la griglia per calcolare la dimensione e l'offset che questa deve avere. 

Le possibili operazioni lunghe e potenzialmente bloccanti, come l'avvio della simulazione o il calcolo per il posizionamento di una linea, vengono eseguite in background in modo da poter mantenere la View reattiva.

La View è disaccoppiata dalla logica di simulazione: essa si limita a tradurre le interazioni utente in ViewMessage inviati al Controller, seguendo il pattern architetturale MVC.


[Indice](../index.md) |
[<](../4-design/index.md) |
[>](../6-testing/index.md)
