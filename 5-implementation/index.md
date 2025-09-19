# Implementazione
Questo capitolo illustra e giustifica le principali decisioni implementative.
Inoltre, all’interno del codice è inclusa la documentazione Scaladoc, utile a descrivere nel dettaglio ogni sua componente.

## Model
### Juri Guglielmi
Il mio contributo si è concentrato sul modulo di propagazione del fuoco, dove ho sviluppato le strategie per il calcolo della probabilità di ignizione e della durata della combustione, e ho progettato un’architettura estendibile che permette di arricchire il modello con effetti ambientali(vento, umidità, presenza di corpi idrici) tramite decoratori funzionali e un DSL basato sul Builder pattern.

#### Scelte Tecniche e Adozione dello Strategy Pattern
L'architettura del modulo adotta i principi della programmazione funzionale, privilegiando l'immutabilità dei dati e le funzioni pure. La griglia di simulazione, infatti, non viene mai modificata: la funzione fireSpread, nucleo della simulazione, riceve lo stato corrente e ne restituisce uno nuovo a ogni ciclo.

Questo approccio disaccoppia l'algoritmo principale dalle logiche specifiche di comportamento delle celle. Tali logiche sono implementate come tipi funzionali, realizzando concretamente lo Strategy Pattern e garantendo che diverse politiche di simulazione possano essere sostituite o combinate con la massima flessibilità.

Nello specifico, sono state definite due strategie principali:
- `ProbabilityCalc`: Una funzione che determina la probabilità di ignizione di una cella, tenendo conto delle sue proprietà, dei parametri globali e del contesto della griglia.
- `BurnDurationPolicy`: Una funzione che stabilisce se una cella che sta bruciando ha esaurito il suo ciclo di vita e deve quindi estinguersi.

```scala
// Strategia per il calcolo della probabilità di ignizione
type ProbabilityCalc = (CellType, SimParams, Position, Matrix) => Double

// Strategia per la durata della combustione
type BurnDurationPolicy = (CellType, Int, Int) => Boolean
```
La funzione `fireSpread` riceve queste strategie come parametri contestuali(`using`), restando agnostica rispetto alla loro implementazione specifica. In questo modo, il comportamento della simulazione può essere modificato semplicemente fornendo una funzione diversa, senza alterare il ciclo principale.

```scala
def fireSpread(
    matrix: Matrix,
    burning: Set[Position],
    params: SimParams,
    currentCycle: Int,
    rng: RNG
)(using
    prob: ProbabilityCalc, // Utilizzo della strategia di probabilità
    burn: BurnDurationPolicy // Utilizzo della strategia di combustione
): (Matrix, Set[Position], RNG) = {
  // ... logica di simulazione ...
}
```
È stata fornita un'implementazione di default per entrambe sia per la `ProbabilityCalc` che per la `BurnDurationPolicy`. La prima,  modella un comportamento di base del fuoco in funzione di parametri come l'infiammabilità della vegetazione, la temperatura, l'umidità e l'influenza delle celle vicine in fiamme, mentre per la seconda ogni cella infiammabile possiede un tempo di durata di combustione e controlla quando siamo arrivati al termine. 
Per migliorare la leggibilità del codice sono stati introdotti alcuni extension method sul tipo CellType, che permettono di accedere direttamente alla vegetazione della cella o di verificarne proprietà rilevanti, come se sia infiammabile o stia bruciando.

#### Modularità degli effetti ambientali
Per modellare fenomeni ambientali complessi che modificano la probabilità di ignizione, come vento o umidità portata dal vento da celle di tipo `Water`, è stato adottato un approccio ispirato al Decorator pattern, implementato in chiave funzionale. In questo contesto, i decoratori sono funzioni di ordine superiore che prendono una `ProbabilityCalc` come input e ne restituiscono una nuova arricchita con logiche aggiuntive, senza modificare lo stato originale.

Ad esempio, la funzione `directionalWindProbabilityDynamic` agisce da decoratore. Prende in input una funzione di probabilità di base `(base: ProbabilityCalc)` e restituisce una nuova funzione che, prima di eseguire il calcolo di base, verifica la presenza di celle in fiamme dalla direzione da cui viene il vento e, in caso affermativo, applica un fattore di potenziamento(`windBoost`) alla probabilità calcolata.

```scala
def directionalWindProbabilityDynamic(base: ProbabilityCalc): ProbabilityCalc =
  (cell, params, pos, matrix) =>
    // ... logica per calcolare il windBoost ...
    val neighborIsBurning =
      matrix.inBounds(rr, cc) && matrix(rr)(cc).isBurning
    val speedFactor = baseWindBoost + math.tanh(
      params.windSpeed / windNormalization
    )
    val windBoost = if neighborIsBurning then speedFactor else baseWindBoost
    
    // Invoca la funzione base e ne modifica il risultato
    val baseProb = base(cell, params, pos, matrix)
    math.min(baseProb * windBoost, maxProbability)
```
Analogamente, `humidityAware` applica una penalità quando l’umidità supera un certo livello, mentre `waterHumidityWind` riduce la probabilità di ignizione per celle riceventi vento umido da corpi idrici vicini. Questo approccio consente di combinare effetti ambientali in maniera modulare e componibile.

#### Composizione delle politiche tramite Builder Pattern
Per combinare i decoratori in modo leggibile e modulare, è stato implementato il Builder Pattern tramite la classe `ProbabilityBuilder`, che espone una DSL fluente.

Il builder parte da una `ProbabilityCalc` di base e offre metodi per applicare i decoratori desiderati in modo sequenziale. Ogni metodo (es. `withWind`, `withWaterEffects`) restituisce una nuova istanza del builder con la funzione di calcolo aggiornata, permettendo di comporre facilmente più effetti senza modificare lo stato originale.

```scala
case class ProbabilityBuilder(private val currentCalc: ProbabilityCalc):

  /** Adds the wind effect */
  def withWind: ProbabilityBuilder =
    copy(currentCalc = directionalWindProbabilityDynamic(currentCalc))

  /** Adds a humidity penalty */
  def withHumidityPenalty: ProbabilityBuilder =
    copy(currentCalc = humidityAware(currentCalc))
  
  // ... altri metodi ...

  /** Finalizes the builder and returns the composed `ProbabilityCalc` */
  def build: ProbabilityCalc = currentCalc
```
L'utilizzo di una `given Conversion` di Scala permette inoltre di passare direttamente un'istanza del `ProbabilityBuilder` laddove è attesa una `ProbabilityCalc`. Ad esempio, per creare una politica che includa vento ed effetti costieri, la sintassi è la seguente:
```scala
val customProbability: ProbabilityCalc = ProbabilityBuilder()
  .withWind
  .withWaterEffects
```

### Riccardo Mazzi
Ho implementato l’interfaccia `Model` e i suoi metodi nella classe `SimModel`, richiamati dal **controller**.
I metodi principali sono:

- `updateState` che fa avanzare la simulazione di un tick.
- `generateMap` che genera la mappa con le dimensioni specificate dall’utente.

#### Rappresentazione della mappa

Per gestire la mappa come matrice di celle, insieme a Nicolò e Juri ho introdotto:

- **`Matrix`** come vettore di vettori di celle, basato sui `Vector` di Scala per sfruttarne l’immutabilità ed evitare _side effects_ dovuti a strutture dati mutabili.
- **`CellType`** come enumerazione che rappresenta i diversi tipi di cella.

All’interno di `Matrix` abbiamo definito anche alcuni **extension methods**, utili a:

- controllare la validità degli indici
- trovare i vicini di una cella
- individuare tutte le posizioni di un certo tipo di celle

#### Generazione della mappa

La logica di generazione della mappa è stata separata dal `SimModel` e spostata nel package `map` per evitare il problema delle _god classes_.

In particolare, ho utilizzato:

- **Pattern Strategy** (`MapGenerationStrategy`)

  - permette di definire facilmente diversi generatori di mappe (attualmente: *base* e *base con fiumi*)
  - semplifica l’estensione futura con algoritmi di generazione più complessi

- **Pattern Builder** (`MapBuilder`)

  - consente di costruire una mappa passo dopo passo
  - no variabili di salvataggio mappa ad ogni step intermedio

- **DSL** (`MapBuilderDSL`)

  - costruzione della mappa più intuitiva
  - sintassi più vicina al linguaggio naturale

La generazione della mappa nel SimModel diventa quindi intuitiva e facilmente personalizzabile:
```scala
matrix = buildMap(rows, cols, random):
      withWater
      withForests
      withGrass
      withStations
      withFires
```

#### Parallelizzazione

I vari step di creazione e aggiornamento della mappa vengono parallelizzati utilizzando `.par` della libreria **Parallel Collections**.
Questa scelta ha portato a un miglioramento delle prestazioni della simulazione, verificato con test manuali.

### Nicolò Monaldini
Mi sono occupato principalmente dell'implementazione dei vigili dei fuoco, in particolare:
- Implementazione della classe `FireFighter`
- Implementazione della monade `ReaderState` utilizzata per aggiornare lo stato dei vigili del fuoco
- Implementazione di funzioni monadiche adibite all'aggiornamento delle istanze `FireFighter`
- Implementazione di un DSL per la creazione di istanze `FireFighter`

#### FireFighter
I vigili del fuoco sono stati pensati come dei record immutabili, per questo motivo sono stati implementati con una case class.
```scala
case class FireFighter(
  station: Position,
  neighborsInRay: Set[Offset],
  target: Position,
  loaded: Boolean,
  steps: LazyList[Position],
  moveStrategy: (Position, Position) => LazyList[Position]
)
```

Ogni istanza di `FireFighter` si muove nella mappa dirigendosi verso celle infuocate circondate da celle erbose o foreste, che sono quelle da salvare. L'obiettivo può essere aggiornato dinamicamente, ad esempio se compaiono celle particolarmente vicine alla stazione o alla posizione attuale o se la cella obiettivo precedente si spegne o non è più circondata da celle salvabili. Il campo `target` rappresenta la cella verso cui sta andando. Un campo booleano `loaded` rappresenta la presenza del carico di acqua e schiuma utilizzato per spegnere il fuoco: nel momento in cui si raggiunge la cella target in fiamme si utilizza il carico per spegnere le celle infuocate in un certo raggio d'azione, rappresentato tramite il campo `neighborsInRay`, un `Set` di offset rispetto alla posizione corrente. Dopo aver utilizzato il carico ogni vigile del fuoco ritorna verso la propria stazione per ricaricarsi e ripartire.

La logica di movimento è separata dalla classe utilizzando il pattern Strategy. Come algoritmo per il movimento è stato utilizzato l'algoritmo della linea di Bresenham, per simulare un movimento realistico.

Il campo `steps` contiene una `LazyList` rappresentante i prossimi passi che il vigile del fuoco dovrà fare. Tale lista è pensata come un iteratore, che arrivato alla traguardo continua a restituire come prossimo elemento il traguardo stesso indefinitamente. Non è stato utilizzato un `Iterator` poiché mutabile, quindi le istanze di `FireFighter` ottenute con il metodo `copy` avrebbero condiviso la stessa istanza.

```scala
def next(x: Int, y: Int, err: Int): (Int, Int, Int) = ...
LazyList.iterate((from._1, from._2, err))(next).map(e => (e._1, e._2))
```
#### Monade ReaderState
Per l'aggiornamento delle istanze `FireFighter` è stata utilizzata una versione modificata della monade `State` vista a lezione. 

La necessità della modifica sorge dal fatto che i `FireFighter` devono essere aggiornati sulla base delle celle infuocate a ogni istante, mentre la monade `State` prevede l'aggiornamento di uno stato solamente a partire dallo stato stesso (il metodo `run`, infatti, prende come argomento solo uno stato `s`).

Traendo spunto dalla monade `Reader` di Haskell, che modella una computazione concatenabile in cui un environment immutabile viene passato tra i vari step concatenati, ho modificato la monade `State` per accettare nel suo metodo `run`, oltre allo stato `s`, anche un environment `e`.
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

#### Aggiornamento di istanze `FireFighter`

Per l'aggiornamento dei vigili del fuoco sono state utilizzate due operazioni monadiche:
- `moveStep`: prende in input le celle infuocate ed un'istanza `FireFighter`, restituisce in output un'istanza con la posizione cambiata e un risultato di tipo `Unit`. Per scegliere la cella verso cui dirigersi viene utilizzata una funzione di scoring lower-is-better, che consiste nella media pesata tra la distanza dalla stazione e dalla posizione attuale del vigile del fuoco. Si è scelto di dare un peso maggiore alla distanza dalla stazione (0.6) rispetto alla distanza dalla posizione attuale (0.4) per dare la priorità alle celle più vicine alla stazione nel caso in cui la media aritmetica tra le due distanze sia uguale per più celle. Nel caso in cui la cella target precedente sia ancora valida, ovvero sia in fiamme e circondata da celle salvabili, si è scelto di richiedere che la nuova cella obiettivo abbia il valore della media pesata inferiore di almeno un decimo rispetto alla corrente, per evitare cambiamenti di obiettivo troppo frequenti.
- `actionStep`: prende in input le celle infuocate e un'istanza `FireFighter`, effettua un'azione che può essere spegnere delle celle infuocate o ricaricare il carico del vigile del fuoco, restituisce in output un'istanza aggiornata ed un `Set` di posizioni di celle che sono state spente.

Queste operazioni sono concatenate in un'unica computazione monadica:
```scala
private val firefightersUpdater =
for
  _ <- moveStep
  extinguishedCells <- actionStep
yield extinguishedCells
```

Per rendere più idiomatiche queste operazioni sono stati utilizzati degli extension methods per la case class `FireFighter`, tra i quali:
- un metodo `position` per ottenere la posizione corrente del vigile del fuoco, ovvero la testa della lista `steps`.
- un'`enum FireFighterAction`, che rappresenta le azioni effettuabili da un `FireFighter` (`Extinguish` e `Reload`) e un metodo `action` che verifica l'azione da svolgere, per poter modellare `actionStep` attraverso un `match-case`. 
- una funzione curried `when` per aggiornare un'istanza di `FireFighter` tramite una funzione di mapping al verificarsi di una condizione, per modellare funzionalmente un aggiornamento condizionale.

Questi accorgimenti migliorano la leggibilità di `moveStep` e `actionStep`, di seguito riportati:

```scala
def moveStep: ReaderState[CellsOnFire, FireFighter, Unit] =
  ReaderState[CellsOnFire, FireFighter, Unit]((fireCells, f) =>
    val newTarget = Option.when(!f.loaded || fireCells.isEmpty)(f.station)
      .getOrElse(
        Option(fireCells)
          .map(_.minBy(f.score))
          .filter(candidate =>
            !fireCells.contains(
              f.target
            ) || f.score(candidate) < f.score(f.target) * correctionThreshold
          ).getOrElse(f.target)
      )
    (f.when(_.target != newTarget)(_ changeTargetTo newTarget).move, ())
  )

def actionStep
    : ReaderState[CellsOnFire, FireFighter, CellsOnFire] =
  ReaderState[CellsOnFire, FireFighter, CellsOnFire]((fireCells, f) =>
    import it.unibo.firesim.model.firefighters.FireFighterUtils.FireFighterAction.*
    f.action(fireCells) match
      case Some(Extinguish) =>
        (
          f.copy(loaded = false),
          f.neighborsInRay.map(d =>
            (d._1 + f.position._1, d._2 + f.position._2)
          ).intersect(fireCells)
        )
      case Some(Reload) => (f.copy(loaded = true), Set.empty[Position])
      case _            => (f, Set.empty[Position])
  )
```

#### Builder e DSL
Per la costruzione delle istanze `FireFighter` è stato utilizzato il pattern Builder. Sebbene nell'implementazione attuale non siano previsti molti campi da inizializzare, l'utilizzo del pattern Builder permette di costruire facilmente un piccolo DSL che lo usi, per sfruttare la significant indentation di Scala per la creazione di istanze di `FireFighter` in modo dichiarativo, come mostrato di seguito:
```scala
createFireFighter:
    withRay(r)
    stationedIn(s)
```
La funzione `createFireFighter` utilizza il meccanismo given/using per prendere in input una context function che richiede un'istanza `given` di tipo `FireFighterBuilder` e che chiamerà funzioni del DSL. 
```scala
def createFireFighter(instructions: FireFighterBuilder ?=> Unit)
    : FireFighter =
  given builder: FireFighterBuilder = FireFighterBuilder()
  instructions(using builder)
  builder.build()

def withRay(ray: Int)(using builder: FireFighterBuilder): Unit =
  builder.withRay(ray)

def stationedIn(s: Position)(using builder: FireFighterBuilder): Unit =
  builder.stationedIn(s)
```

---

## Controller
Come mostrato nelle figure UML, SimController espone pubblicamente solo due metodi dell'interfaccia Controller.

### `handleViewMessage` (Juri Guglielmi)

Il metodo `handleViewMessage` consente alla View di inviare comandi al Controller in modo disaccoppiato. La View crea un oggetto `ViewMessage` e lo passa al Controller, che ne invoca l’esecuzione tramite `msg.execute(this)`. In questo modo, la logica del comando è delegata al messaggio stesso, riducendo l’accoppiamento e facilitando l’aggiunta di nuove operazioni senza modificare la View o il Controller.

ViewMessage è un `sealed trait` il cui insieme di sottoclassi incapsula sia i dati necessari sia la logica di esecuzione tramite il metodo `execute(controller: SimController)`, realizzando concretamente il Command Pattern.

### `loop` (Riccardo Mazzi)

Questo metodo viene chiamato dal main thread e permette al controller di avviare il loop della simulazione.

Il controller salva i millisecondi da aspettare tra un tick e l'altro passati come argomento del metodo.
Tenendo traccia dei valori attuali e originali si potrà poi cambiare la velocità della simulazione a seconda della scelta dell'utente.

Sia il main thread che il thread della view accederanno ad alcune variabili condivise, principalmente flag booleani ma anche le dimensioni della mappa scelte dall'utente e i millisecondi della velocità di simulazione citati prima.
Queste variabili sono dichiarate `volatile` per un accesso thread safe.

Si usa una variabile `lock` per sincronizzare i due thread e permettere al main thread di lasciare le risorse mentre la mappa non è stata ancora generata.

Usando queste variabili thread safe, all'interno del loop il main thread potrà
 - Aspettare prima che la mappa sia generata
 - Usare il metodo `onTick` e quindi chiamare il model per avanzare nella simulazione (se questa sta runnando)
 - Chiamare il metodo `handleQueuedCells` per far inserire al model le celle piazzate dall'utente (dopo aver fatto avanzare il model, assicurando una corretta sincronizzazione).

Per permettere una corretta conversione tra i tipi di celle del model e della view, il Controller offre un enum apposito `CellViewType` e una classe statica per la conversione in entrambi i sensi: `CellTypeConverter`.
Sarà poi la specifica view (indipendente e quindi aggiornabile con facilità) a dover convertire i tipi di cella in colori, testi, o altro da mostrare all'utente.

---

## View
Per realizzare la GUI è stato utilizzato Scala Swing. L'interfaccia grafica è composta da un `Panel` che contiene la griglia con cui l'utente può interagire e da un `BoxPanel` contenente i vari controlli. 

L’utente può interagire con la griglia tramite click e trascinamenti, che consentono il posizionamento delle celle. Le coordinate grafiche ottenute dall’interazione vengono convertite in coordinate logiche attraverso il metodo `pixelToCell`, così da poter essere elaborate dal controller.

I controlli sono organizzati in righe attraverso l'uso di ulteriori `BoxPanel`. Poiché ogni riga ha caratteristiche in comune, come la spaziatura tra i vari controlli, un trait con self-types è stato utilizzato per decorare ogni `BoxPanel`. 

Le varie tipologie di celle sono identificate da un `enum` chiamato `CellViewType`. Le istanze di questa enumerazione sono ricevute dal controller per aggiornare la griglia, e sono utilizzate dalla view per associare un colore a ogni cella.

Uno `SplitPane` è utilizzato per dividere l'area dei controlli dall'area contenente la griglia. Quest'ultima è responsive, e reagisce al ridimensionamento della finestra e al movimento del divisorio dello `SplitPane`. Questa proprietà è ottenuta tramite un metodo `gridGeometry` chiamato all'interno del metodo `paintComponent` del `Panel` contenente la griglia, che utilizza la sua dimensione per calcolare la dimensione e l'offset che la griglia deve avere. 

La View è disaccoppiata dalla logica di simulazione: essa si limita a tradurre le interazioni utente in ViewMessage inviati al Controller, seguendo il pattern architetturale MVC.


[Indice](../index.md) |
[<](../4-design/index.md) |
[>](../6-testing/index.md)
