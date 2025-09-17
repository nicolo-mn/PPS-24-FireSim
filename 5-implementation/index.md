# Implementazione
Questo capitolo illustra e giustifica le principali decisioni implementative.
Inoltre, all’interno del codice è inclusa la documentazione Scaladoc, utile a descrivere nel dettaglio ogni sua componente.

## Model
### Juri Guglielmi

### Riccardo Mazzi
Ho implementato l'interfaccia Model e i suoi metodi in SimModel chiamati dal controller di cui i più importanti sono: `updateState` per far avanzare la simulazione e `generateMap` per generare la mappa con le dimensioni specificate dall'utente.
Per gestire la mappa come matrice di celle, ho creato il tipo `Matrix` e l'enumerazione `CellType` insieme a Nicolò e Juri. `Matrix` è un vettore di vettori di celle, sfruttando l'immutabilità dei `Vector` di Scala ed evitando i _side effect_ di strutture dati mutabili.
In `Matrix` abbiamo anche definito alcuni _extension methods_ utili a controllare indici interni alla matrice, a trovare i vicini di una cella, le posizioni di un certo tipo di celle, ecc.

La generazione della mappa è stata separata dal SimModel e inserita nel package `map`, per evitare _god classes_.
Ho utilizzato il pattern Strategy in `MapGenerationStrategy` per definire facilmente due tipi di generatori (base e base con fiumi) e per permettere in futuro di aggiungere facilmente altri algoritmi di generazione più o meno avanzati.
Ho usato il pattern Builder in `MapBuilder` per poter costruire facilmente una mappa senza doverla salvare ad ogni step sequenziale.
Ho implementato un DSL in `MapBuilderDSL` per rendere la costruzione della mappa ancora più intuitiva e vicina al linguaggio comune.

I diversi step di creazione e update della mappa vengono parallelizzati usando il `.par` della libreria _Parallel Collections_, ottenendo un miglioramento nelle performance della simulazione (verificato con test manuali).

### Nicolò Monaldini
Mi sono occupato principalmente dell'implementazione dei vigili dei fuoco, in particolare:
- Implementazione della classe `FireFighter`
- Implementazione della monade `ReaderState` utilizzata per aggiornare lo stato dei vigili del fuoco
- Implementazione di funzioni monadiche adibite all'aggiornamento delle istanze `FireFighter`
- Implementazione di un piccolo DSL per la creazione di istanze `FireFighter`

#### FireFighter
I vigili del fuoco sono stati pensati come dei record immutabili, per questo motivo sono stati implementati con una case class.
<!-- TODO: Aggiungi codice case class -->
Ogni `FireFighter` ha una posizione e un target, che rappresenta la cella verso cui sta andando. Un campo booleano `loaded` rappresenta la presenta del carico di acqua e schiuma utilizzato per spegnere il fuoco: nel momento in cui si raggiunge la cella infuocata target si utilizza il carico per spegnere le celle infuocate in un certo raggio d'azione, rappresentato tramite il campo `neighborsInRay`, un `Set` di offset rispetto alla posizione corrente.

La logica di movimento è separata dalla classe utilizzando il pattern strategy. Come algoritmo per il movimento è stato utilizzato l'algoritmo della linea di Bresenham, per simulare un movimento realistico.

Il campo `steps` contiene una `LazyList` rappresentante i prossimi passi che il vigile del fuoco dovrà fare. Tale lista pensata come un iteratore, che arrivato alla traguardo continua a restituire come prossimo elemento il traguardo stesso. Non è stato utilizzato un `Iterator` poiché mutabile, quindi le istanze di `FireFighter` ottenute con il metodo `copy` avrebbero condiviso la stessa istanza.

<!-- TODO: Aggiungi codice lazy list -->

#### Monade ReaderState
Per l'aggiornamento delle istanze `FireFighter`, che sono modellate come record, è stata utilizzata una versione modificata della monade `State` vista a lezione. 

La necessità della modifica sorge dal fatto che i `FireFighter` devono essere aggiornati sulla base delle celle infuocate a ogni istante, mentre la monade `State` prevede l'aggiornamento di uno stato solamente a partire dallo stato stesso (il metodo `run`, infatti, prende come argomento solo uno stato `s`).

Ispirandomi alla monade `Reader` di Haskell, che modella una computazione concatenabile in cui un environment immutabile viene passato tra i vari step concatenati, ho modificato la monade `State` per accettare nel suo metodo `run`, oltre allo stato `s`, anche un environment `e`.
```scala
case class ReaderState[E, S, A](run: (E, S) => (S, A))
```
Tale environment sarà poi passato tra le varie operazioni che sono concatenate, come si può vedere nel metodo `flatMap`.
```scala
override def flatMap[B](f: A => ReaderState[E, S, B])
  : ReaderState[E, S, B] =
ReaderState((e, s) =>
  m.apply(e, s) match
    case (s2, a) => f(a).apply(e, s2)
)
```
<!-- TODO: Diagramma? -->
#### Aggiornato di istanze `FireFighter`
Per l'aggiornamento dei vigili del fuoco sono state utilizzate due operazioni monadiche:
- `moveStep`: prende in input le celle infuocate e un vigile del fuoco, restituisce in output il vigile del fuoco aggiornato con la posizione cambiata e un risultato di tipo `Unit`.
- `actionStep`: prende in input le celle infuocate e un vigile del fuoco, effettua un azione che può essere spegnere delle celle infuocate o ricaricare il carico del vigile del fuoco, restituisce in output il vigile del fuoco aggiornato ed un `Set` di celle che sono state spente.

Queste operazioni sono concatenate in un'unica computazione monadica:
```scala
private val firefightersUpdater =
for
  _ <- moveStep
  extinguishedCells <- actionStep
yield extinguishedCells
```

#### Builder e DSL
Per la costruzione delle istanze `FireFighter` è stato utilizzato il pattern builder. Sebbene nell'implementazione attuale non siano previsti molti campi da inizializzare, l'utilizzo del pattern builder permette di costruire facilmente un piccolo DSL che lo usi, tramite la significant indentation di Scala, come mostrato di seguito:
```scala
createFireFighter:
    withRay(ray)
    stationedIn(s)
```
La funzione `createFireFighter` utilizza il meccanismo given/using per prendere in input una context function che necessità di un'istanza given di tipo `FireFigtherBuilder`. 

## Controller
Come mostrato nelle figure UML, SimController espone pubblicamente solo due metodi dell'interfaccia Controller.

### `handleViewMessage` (Juri Guglielmi)

Questo metodo permette alla View di inviare comandi al controller in modo disaccoppiato. La View crea un oggetto `ViewMessage` e lo passa al Controller, che ne invoca l’esecuzione tramite `msg.execute(this)`.In questo modo, la logica specifica del comando è delegata al messaggio stesso, anziché al controller.

Questo approccio utilizza il Command Pattern, che riduce l’accoppiamento tra View e Controller, migliora l’incapsulamento e rende più semplice aggiungere nuove operazioni senza modificare la View né la logica interna del controller.

`ViewMessage` è un sealed trait che rappresenta un comando inviato dalla View al Controller. Ogni sottoclasse incapsula sia i dati necessari per l’operazione sia la logica di esecuzione attraverso il metodo `execute(controller: SimController)`.

Grazie a questo meccanismo, la View non chiama direttamente i metodi del controller, ma si limita a creare e inviare un messaggio, lasciando al comando il compito di agire sul controller.

### `loop` (Riccardo Mazzi)

Questo metodo viene chiamato dal main thread e permette al controller di avviare il loop della simulazione.

Il controller salva i millisecondi da aspettare tra un tick e l'altro passati come argomento del metodo.
Tenendo traccia dei valori attuali e originali si potrà poi cambiare la velocità della simulazione a seconda della scelta dell'utente.

Sia il main thread che il thread della view accederanno ad alcune variabili condivise, principalmente flag booleani ma anche le dimensioni della mappa scelte dall'utente e i millisecondi della velocità di simulazione citati prima.
Queste variabili sono dichiarate con il tag `volatile` per un accesso thread safe.

Per sincronizzare i due thread e permettere al main thread di lasciare le risorse mentre la mappa non è stata ancora generata si usa una variabile `lock`.

Usando queste variabili thread safe, all'interno del loop il main thread potrà aspettare prima che la mappa sia generata,
usare il metodo `onTick` e quindi chiamare il model per avanzare nella simulazione (se questa sta runnando),
e infine chiamare il metodo `handleQueuedCells` per far inserire al model le celle piazzate dall'utente (dopo aver fatto avanzare il model, assicurando una corretta sincronizzazione).

Per permettere una corretta conversione tra i tipi di celle del model e della view, il Controller offre un enum apposito `CellViewType` e una classe statica per la conversione in entrambi i sensi: `CellTypeConverter`.
Sarà poi la specifica view (indipendente e quindi aggiornabile con facilità) a dover convertire i tipi di cella in colori, testi, celle o altro da mostrare all'utente.

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
