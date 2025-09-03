# Processo di Sviluppo Adottato
In questa sezione si descriverà l'organizzazione del team di sviluppo, le metodologie e gli strumenti scelti per la realizzazione del progetto.

## Metodologia Agile: Scrum
Per la gestione del progetto è stata adottata la metodologia **Agile**, implementata attraverso il framework **Scrum**.  
Questo approccio si basa su un processo di sviluppo _iterativo_ e _incrementale_, che permette di fornire valore al committente in modo rapido e costante, adattandosi ai cambiamenti in corso d'opera.

### Ruoli del Team
L'organizzazione del team prevede i seguenti ruoli chiave:

- **Committente**: colui che definisce gli obiettivi di business del progetto.  
  → Ricoperto da **Riccardo Mazzi**.

- **Product Owner**: la figura che massimizza il valore del prodotto, gestendo e prioritizzando la lista di funzionalità da sviluppare (_Product Backlog_).  
  → Ricoperto da **Juri Guglielmi**.

- **Scrum Master**: facilitatore del processo Scrum, responsabile della rimozione di ostacoli e del corretto svolgimento delle attività del team.  
  → Ricoperto da **Nicolò Monaldini**.

---

## Organizzazione degli Sprint
Il ciclo di sviluppo è suddiviso in **Sprint** della durata di **5 giorni**.  
Ogni sprint è scandito da una serie di meeting e attività pianificate:

- **Sprint Planning**: definizione dell’obiettivo dello sprint e selezione dei task dal Product Backlog, che confluiscono nello _Sprint Backlog_.
- **Daily Scrum**: breve riunione giornaliera su Discord per sincronizzare il team, discutere impedimenti e pianificare la giornata.
- **Sprint Review**: presentazione dell’incremento di prodotto al Product Owner e al Committente, con raccolta di feedback.
- **Sprint Retrospective**: incontro conclusivo per riflettere sul processo e individuare possibili miglioramenti.

---

## Strumenti di Collaborazione
Per garantire una comunicazione fluida e una gestione efficiente delle attività, il team utilizza:

- **Discord**: piattaforma principale per le comunicazioni e i meeting pianificati (Planning, Daily, Review, Retrospective).
- **GitHub Projects**: strumento scelto per la gestione dei task. Il Product Backlog consiste in una bacheca in stile **Kanban**, permettendo una visione chiara e condivisa dello stato di avanzamento.

---

## Automazione
Per automatizzare build, testing e gestione delle dipendeze è stato utilizzato *sbt*. *scalafmt* è stato utilizzato per mantenere una formattazione coerente all'interno del progetto.

Per la CI viene utilizzato **GitHub Actions**, che automatizza l’esecuzione dei test a ogni push.

---

## Versioning

- **Semantic Versioning (SemVer)**: le versioni seguono lo schema `MAJOR.MINOR.PATCH`
    - **MAJOR** → cambiamenti non retrocompatibili
    - **MINOR** → aggiunta/modifica retrocompatibile di funzionalità
    - **PATCH** → correzione di bug

- **Conventional Commits**: tutti i messaggi di commit seguono una convenzione standardizzata, migliorando la leggibilità della cronologia e permettendo l’automazione del versioning e la generazione di changelog.


[Indice](../index.md) |
[<](../0-introduction/index.md) |
[>](../2-requirements/index.md)