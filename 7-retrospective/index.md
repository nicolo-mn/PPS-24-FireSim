# Retrospettiva

## Avviamento
In una prima fase ci si è concentrati sulla definizione dei requisiti e sulla produzione di diagrammi UML che definissero l'organizzazione delle principali classi e interfacce del progetto.
Questi UML, dopo una serie di revisioni e correzioni sono stati inseriti nei capitoli di Architettura e Design di dettaglio. 

## Sprint 1 (28/07-1/08)
Nella prima sprint ci si è concentrati sul bootstrap del progetto e sulla definizione delle classi e interfacce principali.
Si è data priorità alla view per poter presentare al committente un prototipo già al termine della prima settimana, come le best practice di programmazione agile comandano.
Si è, inoltre, implementata una prima versione dell'algoritmo di generazione della mappa da integrare con la view.

| Title                                                                                              | Priority | Assignees | Size | Sprint   |
|----------------------------------------------------------------------------------------------------|----------|-----------|------|----------|
| Creation of enums and classes about cells (CellType, CellState etc.)                               | P0       | Mazzi     | S    | Sprint 1 |
| Initial GUI with sliders for parameters, view map update method and popup to select grid dimension | P0       | Monaldini | M    | Sprint 1 |
| Add SimUpdater interface, implementing logic independent from view and model                       | P0       | Guglielmi | S    | Sprint 1 |
| Basic map creation algorithm                                                                       | P1       | Mazzi     | M    | Sprint 1 |
| Implement SimController to receive messages from view                                              | P1       | Guglielmi | S    | Sprint 1 |
| GUI parameter selectors buttons                                                                    | P1       | Monaldini | S    | Sprint 1 |


## Sprint 2 (04/08- 08/08)
Nella seconda sprint si sono aggiunti i vigili del fuoco e l'algoritmo di diffusione del fuoco nella parte di model.
Abbiamo implementato una versione minimale del loop della simulazione, per avere il prima possibile una prima versione eseguibile della simulazione.

| Title                                                                                                                                    | Priority | Assignees | Size | Sprint   |
|------------------------------------------------------------------------------------------------------------------------------------------|----------|-----------|------|----------|
| Manage FireFighter logic                                                                                                                 | P0       | Monaldini | L    | Sprint 2 |
| Fire diffusion                                                                                                                           | P0       | Guglielmi | M    | Sprint 2 |
| Implement SimUpdater loop without buttons management                                                                                     | P0       | Mazzi     | M    | Sprint 2 |
| Implement preliminary stages of SimController (e.g. map generation, fire placement, etc.), Add params management in view and controller  | P0       | Mazzi     | S    | Sprint 2 |
| Make SimParams and correct SimModel interface (with tests up to date)                                                                    | P0       | Guglielmi | S    | Sprint 2 |

## Sprint 3 (11/08-15/08)
Nella terza sprint si è completata la logica del piazzamento delle celle, e si è deciso di svolgere un refactoring sulla struttura della matrice che rappresenta la mappa della simulazione, giungendo alla versione finale che consiste in una matrice di `CellType`.
Sono state fatte migliorie anche alla view aggiungendo la possibilità di tracciare linee da due punti.

| Title                                                                                       | Priority | Assignees | Size | Sprint   |
|---------------------------------------------------------------------------------------------|----------|-----------|------|----------|
| Cell placing logic in model                                                                 | P0       | Mazzi     | S    | Sprint 3 |
| Update state, finish first working SimModel                                                 | P0       | Mazzi     | S    | Sprint 3 |
| Remove Cell, use CellType instead                                                           | P0       | Monaldini | S    | Sprint 3 |
| Add Rock in CellType, adjust map generation. Add FireFighter cell types and its conversions | P0       | Monaldini | S    | Sprint 3 |
| Calculate barrier line from 2 points                                                        | P0       | Guglielmi | S    | Sprint 3 |
| Change SimParams types and update                                                           | P0       | Guglielmi | S    | Sprint 3 |

## Sprint 4 (18/08- 22/08)
La quarta sprint si è concentrata sull'aggiunta di ulteriori utility di view, in particolare il pennello per cambiare il tipo a più celle contemporaneamente.
Si è deciso di fare alcuni raffinamenti nell'algoritmo di generazione mappa aggiungendo laghi, nel movimento dei vigili del fuoco per utilizzare movimenti realistici attraverso un algoritmo di rasterizzazione e dell'impatto del vento nella diffusione del fuoco.
È stata aggiunta anche la possibilità di modificare la velocità della simulazione.

| Title                                                            | Priority | Assignees | Size | Sprint   |
|------------------------------------------------------------------|----------|-----------|------|----------|
| Firefighter more realistic movement                              | P1       | Monaldini | S    | Sprint 4 |
| Add lakes in map generation                                      | P1       | Mazzi     | S    | Sprint 4 |
| Changing the impact of wind direction                            | P1       | Guglielmi | S    | Sprint 4 |
| Simulation speed management                                      | P2       | Mazzi     | S    | Sprint 4 |
| Brush drawing in map                                             | P2       | Monaldini | S    | Sprint 4 |
| Handle draw line button that works for every type of cell        | P2       | Guglielmi | M    | Sprint 4 |

## Sprint 5 (24/08-29/08)
La quinta sprint si è concentrata sul refactoring della view, riducendo il numero di componenti Swing utilizzati evitando lag.
Si è inoltre deciso di modificare la logica di movimento dei vigili del fuoco per dare la priorità alle celle più vicine alla propria stazione.
Tramite l'uso di `scala-parallel-collections` si è parallelizzato l'algoritmo di map generation e l'aggiornamento dei vigili del fuoco, per rendere l'esperienza d'uso più fluida, permettendo di utilizzare mappe con dimensione maggiore senza lag.

| Title                                                                        | Priority | Assignees | Size | Sprint   |
|------------------------------------------------------------------------------|----------|-----------|------|----------|
| Refactor view to use a single frame instead of a matrix of buttons           | P1       | Guglielmi | M    | Sprint 5 |
| Change firefighter behavior to prioritize cells closer to their fire station | P1       | Monaldini | S    | Sprint 5 |
| Parallelize map generation                                                   | P2       | Mazzi     | S    | Sprint 5 |
| Parallelize fire fighters                                                    | P2       | Monaldini | S    | Sprint 5 |
| Change firefighter logic to improve performance                              | P2       | Monaldini | S    | Sprint 5 |

## Sprint 6 (08/09-12/09)
Alla luce di numerosi test manuali svolti sull'applicazione, si sono individuati alcuni bug riguardanti l'utilizzo degli strumenti di disegno e alcune modifiche minori per migliorare l'esperienza di utilizzo.
Sono stati svolti dei refactoring per migliorare la modularità dei componenti e la qualità del codice.

| Title                                                                                             | Priority | Assignees | Size | Sprint   |
|---------------------------------------------------------------------------------------------------|----------|-----------|------|----------|
| Fix controller converting burning cells                                                           | P1       | Mazzi     | XS   | Sprint 6 |
| Handle minimum GUI window size                                                                    | P2       | Monaldini | XS   | Sprint 6 |
| Set minimum and maximum map dimension                                                             | P2       | Monaldini | XS   | Sprint 6 |
| Fix application closing not working in GUI dialog                                                 | P2       | Guglielmi | XS   | Sprint 6 |
| Modify pause/resume button text depending on its state                                            | P2       | Guglielmi | XS   | Sprint 6 |
| Handle conflicts when both brush and draw line are active                                         | P2       | Monaldini | XS   | Sprint 6 |
| Refactor of map generation into package "map"                                                     | P2       | Mazzi     | M    | Sprint 6 |
| Make FireFighter use a weighted average for target selection and make its strategy use a lazylist | P2       | Monaldini | S    | Sprint 6 |

## Commenti finali
Complessivamente il team è soddisfatto del lavoro svolto.
Nonostante le difficoltà incontrate durante lo sviluppo e l'utilizzo di tecnologie mai usate in un progetto prima d'ora, il team è riuscito a completare i requisiti stabiliti inizialmente.
La simulazione risulta interattiva e verosimile, rendendo FireSim un ottimo strumento educativo.
Come sviluppi futuri si potrà aumentare il realismo della logica e dell'interfaccia grafica.

[Indice](../index.md) |
[<](../6-testing/index.md)
