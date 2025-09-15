# Testing
Il progetto è stato sviluppando applicando i principi del Test Driven Development (TDD), con test prodotti a pari passo con le parti implementate. I test si sono concentrati principalmente sulla parte di model e su funzioni di utilità. 

## Tecnologie utilizzate
La tecnologia utilizzata per svolgere i test è stata *ScalaTest*. È stato fatto largo uso di *FlatSpec*, che promuove una strutturazione leggibile di test, che vengono descritti tramite frasi che ricordano il linguaggio naturale, e *Matchers*, che fornisce un linguaggio di asserzioni più espressivo rispetto al classico `assert`. Pratiche di Continuous Integration sono state utilizzate tramite workflow di GitHub Actions per garantire che tutti i test passassero a ogni push, eseguendo il commando `sbt test`. 

Per verificare la copertura dei test è stato utilizzato `sbt-scoverage` che ci ha permesso di tracciare le aree più e meno testate del nostro codice, incrementando il numero e il dettaglio dei test dove necessario.

## Esempi rilevanti
<!-- TODO: quali test mettiamo? -->


[Indice](../index.md) |
[<](../5-implementation/index.md) |
[>](../7-retrospective/index.md)
