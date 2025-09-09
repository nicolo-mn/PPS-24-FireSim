# Requisiti

## Requisiti di business
   L'obiettivo è creare un simulatore configurabile e visivamente intuitivo per analizzare la dinamica degli incendi boschivi.
   Il software deve servire come strumento educativo e di analisi, permettendo di comprendere l'impatto di variabili ambientali e strategie di contrasto sulla propagazione di un incendio.

   Il modello deve permettere l’osservazione dell’evoluzione dell’incendio in una foresta semplificata, sotto l’influenza di parametri ambientali (come vento, temperatura, umidità).
   Inoltre deve considerare i meccanismi di contrasto quali agenti di intervento contro l’incendio (vigili del fuoco) e possibili linee di frangifuoco definite dall’utente.

   La mappa generata dovrà essere personalizzabile prima dell'inizio della simulazione, per poter modellare determinati biomi.
   I parametri saranno modificabili anche durante la simulazione, per poter modellare il cambiamento di essi durante la propagazione dell'incendio.
   Le linee di frangifuoco devono poter essere posizionate durante il corso della simulazione, così come ulteriori incendi, simulando incendi spontanei che appaiono gradualmente.

   Nella fase iniziale è possibile posizionare delle stazioni dei vigili del fuoco in zone di interesse, ognuna dei quali avrà un'unità operativa con l'obiettivo di proteggere l'area circostante alla propria stazione.

## Modello di dominio
   Il dominio del sistema è composto dai seguenti concetti chiave:
- Mappa: Una griglia 2D che rappresenta il territorio, composta da celle con tipi di terreno diversi.
- Cella: L'unità atomica della mappa, rappresenta un tipo di terreno (es. boschivo, roccioso, acqua, in fiamme, bruciato, stazione, ecc.).
- Agente di contrasto: Un'entità (es. squadra di pompieri) che si muove sulla mappa partendo da una stazione per estinguere le celle in fiamme più vicine.
- Parametri di simulazione: L'insieme delle variabili che influenzano la simulazione, tra cui vento (intensità e direzione), umidità e temperatura.

## Requisiti funzionali
### Utente
1. Personalizzazione della dimensione della mappa da generare
2. Generazione di una mappa realistica
   1. Base rocciosa
   2. Cluster di tipo foresta per simulare gruppi di alberi (minimo 1)
   3. Contorni di tipo erba attorno alle foreste
   4. Cluster di tipo acqua per simulare laghi
   5. Stazioni che preferibilmente si ergono su terreni di tipo roccioso (minimo 1)
3. Possibilità di ridimensionare la finestra GUI
4. Personalizzazione della mappa generata prima dell'inizio della simulazione
   1. Componenti UI che permettono la scelta del tipo di terreno da piazzare
   2. Utilizzo di strumenti di disegno
      1. Default: modifica di una singola cella tramite click
      2. Strumento "linea": modifica di più celle su una linea (orizzontale, verticale o obliqua) dopo averne selezionato i 2 estremi
      3. Strumento "pennello": Strumento "pennello": dopo il primo clic, modifica tutte le celle su cui passa il puntatore, fino a un secondo clic che disattiva lo strumento
5. Personalizzazione dei parametri prima dell'inizio e durante la simulazione (direzione e intensità del vento, umidità, temperatura)
6. Possibilità di avviare, mettere in pausa, riprendere e fermare la simulazione
7. Rappresentazione grafica della simulazione in griglia 2D
   1. Rappresentazione dei diversi tipi di terreno con un determinato colore
   2. Gli agenti di contrasto sono rappresentati anch'essi con un colore caratteristico, che sovrascrive il colore della cella su cui è posizionato
8. Controllo del flusso temporale della simulazione (0.5x, 1x, 1.5x, 2x)
9. Rappresentazione della corretta diffusione del fuoco
   1. Solo i tipi di terreno infiammabili (boschivo, erboso) possono diventare _in fiamme_
   2. Una cella che diventa _in fiamme_ ha 3 stadi del fuoco
      1.  Si ha un primo stadio di ignezione in cui la probabilità di infuocare un'altra è abbastanza bassa
      2.  Il secondo stadio è quello in cui il fuoco è più vivo ed ha una maggiore probabilità di infuocare
         un'altra cella
      3. L'ultimo stadio è quello in cui il fuoco ha meno probabilità di infuocare una cella vicina 
   4. Dopo un certo tempo, una cella _in fiamme_ diventa bruciata senza possibilità di bruciare nuovamente
      1. Il tipo di terreno boschivo brucierà più a lungo ma prenderà fuoco più difficilmente
      2. Il tipo di terreno erboso brucierà per meno tempo ma prenderà fuoco più facilmente
   5. All'aumentare della temperatura, la diffusione sarà più veloce
   6. All'aumentare dell'umidità, la diffusione sarà più lenta
   7. All'aumentare dei vicini in fiamme, aumentà la probabilità di prendere fuoco
   8. All'aumentare dell'intensità del vento, la diffusione del fuoco sarà maggiore nella direzione del vento
10. Le squadre di pompieri:
    1. Si muovono verso celle infuocate che hanno attorno celle erboste o foreste, con l'obiettivo di proteggerle, dando la priorità a quelle più vicino alla loro stazione
    2. Si muovono su tutta la mappa senza vincoli, in modo realistico seguendo visivamente la diagonale tra il punto in cui si trovano e l'obiettivo 
    3. Una volta raggiunto l'obiettivo, spengono tutte le celle nell'intorno di quella su cui si trovano, a seconda del loro raggio di azione, consumando le proprie risorse
    4. Dopo aver agito, le squadre di pompieri ritornano alla propria stazione per rifornirsi

### Sistema

1. Generazione randomica della mappa a partire dalle dimensioni fornite
   1. Limite inferiore per garantire i vincoli della generazione (minimo 1 foresta e 1 stazione)
   2. Limite superiore per poter mostrare correttamente la mappa nella sua interezza
2. Propagazione del fuoco basandosi su un modello probabilistico e controllando le celle adiacenti a quelle _in fiamme_
   1. Esclusione delle celle non infiammabili
   2. Modifica della probabilità a seconda del tipo di terreno
   3. Modifica della probabilità a seconda dei parametri
3. Utilizzo di un contatore interno per trasformare le celle _in fiamme_ in _bruciate_ dopo un certo tempo
4. Gli agenti:
   1. Si muovono a passi unitari, spostandosi in una delle 8-celle nell'intorno della posizione attuale (o restando fermi in caso non vi siano celle in fiamme)
   2. Il movimento segue un algoritmo di rasterizzazione.
   3. Si muovono verso la cella infuocata (con attorno celle erboste o foreste) più vicina alla propria stazione
   4. Durante il movimento di un agente, l'obiettivo viene cambiato in caso celle infuocate più vicine alla stazione prendano fuoco
5. Le linee piazzate dall'utente seguono un algoritmo di rasterizzazione

## Requisiti non funzionali

1. Performance: La simulazione deve essere fluida (almneno 30 TPS) su una mappa di dimensioni standard (almeno 100x100 celle) con un numero moderato di agenti.
Requisiti minimi di 4GB di RAM, CPU Quad Core da 3.6 GHz
2. Usabilità: L'interfaccia grafica (GUI) deve essere minimale ma intuitiva, permettendo un facile accesso a tutte le funzionalità di controllo della simulazione
3. GUI reattiva

## Requisiti di implementazione
1. Il progetto dovrà essere espandibile e modulare, permettendo l’aggiunta di ulteriori funzionalità
2. Si dovrà garantire una separazione tra logica e interfaccia utente per agevolare future modifiche a queste, per cui il progetto seguirà il pattern MVC (Model-View-Controller)
3. Il progetto sarà sviluppato interamente in Scala e testato con Scalatest 
4. Si utilizzerà Swing per la creazione dell'interfaccia grafica
5. Si seguierà il TDD (Test Driven Development)
6. Si userà Scalafix e Scala Formatter per controllare e mantenere uniformità nel codice.

[//]: # (7. Si userà SCoverage per monitorare la copertura dei test. )


[Indice](../index.md) |
[<](../1-development-process/index.md) |
[>](../3-architecture/index.md)
