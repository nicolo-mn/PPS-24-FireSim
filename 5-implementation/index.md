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
Questa soluzione elegante permette alla view di inviare messaggi al controller evitando di esporre troppi metodi diversi tra loro,
migliorando l'incapsulamento del controller.
[//]: # (aggiungi juri)

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

L'utente può interagire con la griglia con click e trascinamenti che permettono il posizionamento di celle, convertiti in coordinate logiche da `pixelToCell` e inviati al controller sotto forma di messaggi `ViewMessage`.

I controlli sono organizzati in righe attraverso l'uso di ulteriori `BoxPanel`. Poiché ogni riga ha caratteristiche in comune, come la spaziatura tra i vari controlli, un trait con self-types è stato utilizzato per decorare ogni `BoxPanel`. 

Le varie tipologie di celle sono identificate da un `enum` chiamato `CellViewType`. Le istanze di questa enumerazione sono ricevute dal controller per aggiornare la griglia, e sono utilizzate dalla view per associare un colore a ogni cella.

Uno `SplitPane` è utilizzato per dividere le due aree. La griglia è responsive, e reagisce al ridimensionamento della finestra e delle due aree. Questo è ottenuto tramite un metodo `gridGeometry` chiamato all'interno del metodo `paintComponent` del `Panel`, che utilizza la dimensione del `Panel` contenente la griglia per calcolare la dimensione e l'offset che questa deve avere. 

Le possibili operazioni lunghe e potenzialmente bloccanti, come l'avvio della simulazione o il calcolo per il posizionamento di una linea, vengono eseguite in background in modo da poter mantenere la View reattiva.




[Indice](../index.md) |
[<](../4-design/index.md) |
[>](../6-testing/index.md)
