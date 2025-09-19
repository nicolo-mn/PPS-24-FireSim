# Requisiti

## Requisiti di business
   L'obiettivo è creare un simulatore configurabile e visivamente intuitivo per analizzare la dinamica degli incendi boschivi.
   Il software deve servire come strumento educativo e di analisi, permettendo di comprendere l'impatto di variabili ambientali e strategie di contrasto sulla propagazione di un incendio.

   Il modello deve permettere l’osservazione dell’evoluzione semplificata di un incendio in una foresta, sotto l’influenza di parametri ambientali (come vento, temperatura, umidità).
   Inoltre deve considerare i meccanismi di contrasto quali i vigili del fuoco contro l’incendio e possibili linee di frangifuoco definite dall’utente.

   La mappa generata dovrà essere personalizzabile prima dell'inizio della simulazione, per poter modellare determinati biomi.
   I parametri saranno modificabili anche durante la simulazione, per poter modellare il cambiamento di essi durante la propagazione dell'incendio.
   Le linee di frangifuoco devono poter essere posizionate durante il corso della simulazione, così come ulteriori incendi, simulando incendi spontanei che appaiono gradualmente.

   Nella fase iniziale è possibile posizionare delle stazioni dei vigili del fuoco in zone di interesse, ognuna dei quali avrà un'unità operativa con l'obiettivo di proteggere l'area circostante alla propria stazione.

## Modello di dominio
   Il dominio del sistema è composto dai seguenti concetti chiave:
- Mappa: Una griglia 2D che rappresenta il territorio, composta da celle con tipi di terreno diversi.
- Cella: L'unità atomica della mappa, rappresenta un tipo di terreno (foresta, prato, barriera, stazione, acqua, in fiamme, bruciato, roccia).
- Pompieri: Un'entità che si muove sulla mappa partendo da una stazione per estinguere le celle in fiamme più vicine.
- Parametri di simulazione: L'insieme delle variabili che influenzano la simulazione, tra cui vento (intensità e direzione), umidità e temperatura.

## Requisiti funzionali
### Utente
1. Personalizzazione della dimensione della mappa da generare
2. Generazione di una mappa realistica
   1. Base rocciosa
   2. Cluster di tipo foresta per simulare gruppi di alberi (minimo 1)
   3. Contorni di tipo erba attorno alle foreste
   4. Cluster di tipo acqua per simulare laghi (minimo 1)
   5. Percorsi di tipo acqua per simulare fiumi (minimo 1)
      1. Partendo dal centro dei laghi fino a toccare il bordo della mappa
      2. Cambiando casualmente direzione e larghezza per creare forme verosimili
   6. Stazioni che preferibilmente si ergono su terreni di tipo roccioso (minimo 1)
3. Possibilità di ridimensionare la finestra GUI; la mappa si ridimensionerà dinamicamente
4. Personalizzazione della mappa generata
   1. Componente UI che permette la scelta del tipo di terreno da piazzare
      1. Prima dell'inizio della simulazione i tipi di terreno: in fiamme, barriera, foresta, prato, roccia, acqua e stazione 
      2. Dopo l'inizio della simulazione i tipi di terreno: in fiamme e barriera
   2. Utilizzo di strumenti di disegno
      1. Default: modifica di una singola cella tramite click
      2. Strumento "linea": modifica di più celle su una linea (orizzontale, verticale od obliqua) dopo averne selezionato i 2 estremi
      3. Strumento "pennello": modifica tutte le celle su cui passa il puntatore con il click del mouse tenuto premuto
   3. Quando l'utente seleziona uno strumento di disegno, gli altri devono essere disattivati e non selezionabili
   4. L'utente può piazzare celle in fiamme solo su celle infiammabili (foresta o prato)
5. Personalizzazione dei parametri prima dell'inizio e durante la simulazione (direzione e intensità del vento, umidità, temperatura)
   1. Ogni parametro avrà uno slider dedicato, con accanto indicato il valore selezionato, modificabile in qualsiasi momento dall'utente
6. Controlli per gestire il flusso temporale della simulazione
   1. Un selettore per modificare della velocità (0.5x, 1x, 1.5x, 2x)
   2. Un bottone per avviare la simulazione
   3. Un bottone per mettere in pausa e riprendere la simulazione, in cui cambierà il testo a seconda dello stato attuale
7. Quando la simulazione non è stata ancora avviata, i tasti per fermare/riprendere la simulazione e il selettore di velocità devono essere disattivati
8. Quando la simulazione è avviata, il tasto per avviare la simulazione deve essere disattivato
9. Il fuoco si espande solo su celle infiammabili (erba e foreste)
10. Rappresentazione grafica della simulazione in griglia 2D
    1. Rappresentazione dei diversi tipi di terreno con un determinato colore
    2. I vigili di fuoco sono rappresentati anch'essi con uno specifico colore, che sovrascrive il colore della cella su cui è posizionato
11. Rappresentazione della corretta diffusione del fuoco
    1. Solo i tipi di terreno infiammabili (boschivo, erboso) possono diventare _in fiamme_
    2. Una cella che diventa _in fiamme_ ha 3 stadi del fuoco
       1.  Si ha un primo stadio di ignizione in cui la probabilità di infuocare un'altra è abbastanza bassa
       2.  Il secondo stadio è quello in cui il fuoco è più vivo ed ha una maggiore probabilità di infuocare
          un'altra cella
       3. L'ultimo stadio è quello in cui il fuoco ha meno probabilità di infuocare una cella vicina 
    3. Dopo un certo tempo, una cella _in fiamme_ diventa bruciata senza possibilità di bruciare nuovamente
       1. Il tipo di terreno boschivo brucerà più a lungo ma prenderà fuoco più difficilmente
       2. Il tipo di terreno erboso brucerà per meno tempo ma prenderà fuoco più facilmente
    4. All'aumentare della temperatura, la diffusione sarà più veloce
    5. All'aumentare dell'umidità, la diffusione sarà più lenta
    6. All'aumentare dei vicini in fiamme, aumentà la probabilità di prendere fuoco
    7. All'aumentare dell'intensità del vento, la diffusione del fuoco sarà maggiore nella direzione del vento
12. Ogni cella stazione avrà un vigile del fuoco, adibito alla protezione dell'area circostante
13. I vigili del fuoco:
    1. Si muovono verso celle infuocate che hanno attorno celle erbose o foreste, con l'obiettivo di proteggerle, dando la priorità a quelle più vicine alla loro stazione
    2. Si muovono su tutta la mappa senza vincoli (come unità aeree), seguendo visivamente la diagonale tra il punto in cui si trovano e l'obiettivo 
    3. Si muovono indipendentemente, ognuno dando la priorità al terreno attorno alla sua stazione
    4. Una volta raggiunto l'obiettivo, spengono tutte le celle nell'intorno di quella su cui si trovano, a seconda del loro raggio di azione, consumando le proprie risorse
    5. Dopo aver agito, i vigili del fuoco ritornano alla propria stazione per rifornirsi
    6. Se non sono presenti celle infuocate, ritornano alla propria stazione

### Sistema

1. Generazione randomica della mappa a partire dalle dimensioni fornite
   1. Limite inferiore per garantire i vincoli della generazione (minimo 1 foresta, 1 stazione e 1 zona d'acqua)
   2. Limite superiore per poter mostrare correttamente la mappa nella sua interezza
   3. Generazione di seeds per i vari tipi di terreno e sparsi nella mappa
   4. Crescita dei cluster probabilistica a partire dai seed
   5. Algoritmi di path finding con modifica casuale di direzione e larghezza per costruire fiumi realistici
2. Propagazione del fuoco basandosi su un modello probabilistico e controllando le celle adiacenti a quelle _in fiamme_
   1. Esclusione delle celle non infiammabili
   2. Modifica della probabilità a seconda del tipo di terreno
   3. Modifica della probabilità a seconda dei parametri
3. Utilizzo di un contatore interno per trasformare le celle _in fiamme_ in _bruciate_ dopo un certo tempo
4. I vigili del fuoco:
   1. Si muovono a passi unitari, spostandosi in una delle 8-celle nell'intorno della posizione attuale
   2. Il movimento segue un algoritmo di rasterizzazione
   3. Partendo dalla stazione, si muovono verso la cella infuocata (con attorno celle erbose o foreste) più vicina alla propria stazione
   4. Durante il movimento di un vigile del fuoco, l'obiettivo può essere cambiato dinamicamente in caso celle infuocate vicine alla stazione o alla posizione attuale prendano fuoco
5. Le linee piazzate dall'utente seguono un algoritmo di rasterizzazione

## Requisiti non funzionali

1. Performance: La simulazione deve essere fluida (almeno 30 TPS) su una mappa di almeno 100x100 celle 
con 3 vigili del fuoco (requisiti minimi: 4GB di RAM, CPU Quad Core da 3.6 GHz).
2. Usabilità: L'interfaccia grafica (GUI) sarà minimale 
3. GUI intuitiva per cui l'utente potrà fin da subito capire facilmente tutte le funzionalità di controllo della simulazione
4. GUI reattiva senza grossi lag o delay tra input dell'utente e output a schermo

## Requisiti di implementazione
1. Il progetto dovrà essere espandibile e modulare, facilitando l’aggiunta di ulteriori funzionalità
2. Si dovrà garantire una separazione tra logica e interfaccia utente per agevolare future modifiche a queste, per cui il progetto seguirà il pattern MVC (Model-View-Controller)
3. Il progetto sarà sviluppato interamente in Scala e testato con Scalatest 
4. Si utilizzerà Swing per la creazione dell'interfaccia grafica
5. Si seguirà il TDD (Test Driven Development)
6. Si userà Scalafix e Scala Formatter per controllare e mantenere uniformità nel codice.
7. Si userà SCoverage per monitorare la copertura dei test.


[Indice](../index.md) |
[<](../1-development-process/index.md) |
[>](../3-architecture/index.md)
