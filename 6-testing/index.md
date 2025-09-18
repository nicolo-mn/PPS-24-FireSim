# Testing
Il progetto è stato sviluppando applicando i principi del Test Driven Development (TDD), con test prodotti a pari passo con le parti implementate.
I test si sono concentrati principalmente sulla parte di model e su funzioni di utilità. 

## Tecnologie utilizzate
La tecnologia utilizzata per svolgere i test è stata *ScalaTest*.
È stato fatto largo uso di:
 - *FlatSpec*, che promuove una strutturazione leggibile di test perché descritti tramite frasi che ricordano il linguaggio naturale
 - *Matchers*, che fornisce un linguaggio di asserzioni più espressivo rispetto al classico `assert`
Pratiche di Continuous Integration sono state utilizzate tramite workflow di GitHub Actions per garantire che tutti i test passassero a ogni push, eseguendo il commando `sbt test`. 

Per verificare la copertura dei test è stato utilizzato `sbt-scoverage` che ci ha permesso di tracciare le aree più e meno testate del nostro codice, incrementando il numero e il dettaglio dei test dove necessario.

## Esempi rilevanti
<!-- TODO: quali test mettiamo? -->
```scala
"FireFighterBuilder" should "throw an exception if ray is negative" in {
    val builder = new FireFighterBuilder
    an[IllegalArgumentException] should be thrownBy builder.withRay(-1)
}

it should "throw an exception if stationed in negative coordinates" in {
    val builder = new FireFighterBuilder
    an[IllegalArgumentException] should be thrownBy builder.stationedIn((-1, 0))
    an[IllegalArgumentException] should be thrownBy builder.stationedIn((0, -5))
}
```
```scala
"fireSpread" should "ignite adjacent flammable cells when probability is max" in {
    val matrix: Matrix = Vector(
      Vector(
        CellType.Grass,
        CellType.Burning(0, FireStage.Active, CellType.Grass)
      ),
      Vector(CellType.Grass, CellType.Grass)
    )

    val (result, newBurning, _) =
      fireSpread(matrix, Set((0, 1)), params, 1, rng)

    result(0)(0) shouldBe a[CellType.Burning]
    result(0)(0).asInstanceOf[CellType.Burning].originalType shouldBe Grass
    result(1)(0) shouldBe a[CellType.Burning]
  }
```
[Indice](../index.md) |
[<](../5-implementation/index.md) |
[>](../7-retrospective/index.md)
