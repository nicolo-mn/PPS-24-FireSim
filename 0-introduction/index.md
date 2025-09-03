# Introduzione

**Membri:**
- Juri Guglielmi (juri.guglielmi@studio.unibo.it)
- Riccardo Mazzi (riccardo.mazzi@studio.unibo.it)
- Nicolò Monaldini (nicolo.monaldini@studio.unibo.it)

**Deadline:** 19/09/2025

**Requisiti e funzionalità principali:**  
Il progetto mira a realizzare un simulatore di incendi boschivi su mappa 2D, nel quale l’utente può definire i parametri riguardanti la propagazione e il contrasto degli incendi.

La mappa viene creata da un algoritmo all’avvio del programma e contiene principalmente aree boschive e stazioni di pronto intervento. Diversi agenti partono da queste stazioni e contrastano gli incendi che si innescano (in modo casuale o a scelta dall’utente).

Gli incendi si propagano dalle celle in fiamme a quelle infiammabili adiacenti secondo regole probabilistiche influenzate dai parametri della simulazione (intensità e direzione del vento, temperatura e umidità dell’aria, ecc.).

**Caratteristiche base**
- Algoritmo di creazione randomica delle mappe
- Algoritmo di propagazione del fuoco
- Implementazione di meccanismi di contrasto del fuoco
- Possibilità di definire i parametri principali
- Possibilità di controllare il tempo di simulazione (pause/resume, velocità)
- GUI minimale

**Caratteristiche aggiuntive**
- Aggiunta di ulteriori elementi naturali come fiumi, aree montuose e laghi
- Creazione manuale e personalizzazione della mappa
- Possibilità di modifica dei parametri durante l’esecuzione della simulazione (temperatura, umidità e vento dinamici)
- Aggiunta di ulteriori algoritmi per la creazione randomica delle mappe

[Indice](../index.md) |
[>](../1-development-process/index.md)