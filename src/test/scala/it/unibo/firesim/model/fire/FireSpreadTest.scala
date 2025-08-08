package it.unibo.firesim.model.fire

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.{Cell, CellType}

class FireSpreadTest extends AnyFunSuite with Matchers:

  test(
    "a flammable cell becomes burning with high probability and burning neighbor"
  ):
    val matrix: Matrix = Vector(
      Vector(Cell(0, 0, CellType.Grass), Cell(0, 1, CellType.Burning(0))),
      Vector(Cell(1, 0, CellType.Grass), Cell(1, 1, CellType.Grass))
    )
    val params = SimParams(
      windSpeed = 0.0,
      windAngle = 0.0,
      temperature = 30.0,
      humidity = 10.0
    )

    given prob: ProbabilityCalc = (_, _, _, _, _) => 1.0
    given burn: BurnDurationPolicy = (_, _) => false
    given rand: RandomProvider = () => 0.0

    val result = fireSpread(matrix, params, 1)
    result(0)(0).cellType shouldBe CellType.Burning(1)
    result(1)(0).cellType shouldBe CellType.Burning(1)

  test("a burning cell becomes burnt after enough cycles"):
    val matrix: Matrix = Vector(
      Vector(Cell(0, 0, CellType.Burning(0)))
    )
    val params = SimParams(0, 0, 20, 50)
  
    given prob: ProbabilityCalc = (_, _, _, _, _) => 0.0
    given burn: BurnDurationPolicy = (start, current) => (current - start) >= 3
    given rand: RandomProvider = () => 1.0
  
    fireSpread(matrix, params, 3)(using prob, burn, rand)(0)(0).cellType shouldBe CellType.Burnt
  
  test("a flammable cell does not burn if probability is zero"):
    val matrix: Matrix = Vector(
      Vector(Cell(0, 0, CellType.Grass), Cell(0, 1, CellType.Burning(0)))
    )
    val params = SimParams(0, 0, 20, 50)
  
    given prob: ProbabilityCalc = (_, _, _, _, _) => 0.0
    given burn: BurnDurationPolicy = (_, _) => false
    given rand: RandomProvider = () => 0.0
  
    fireSpread(matrix, params, 1)(using prob, burn, rand)(0)(0).cellType shouldBe CellType.Grass

  test("defaultProbabilityCalc gives higher probability for forest than grass") {
    val forest = Cell(0, 0, CellType.Forest)
    val grass = Cell(0, 1, CellType.Grass)
    val dummyMatrix = Vector.empty
    val params = SimParams(windSpeed = 0, windAngle = 0, temperature = 30, humidity = 20)

    val forestProb = defaultProbabilityCalc(forest, params, 0, 0, dummyMatrix)
    val grassProb = defaultProbabilityCalc(grass, params, 0, 1, dummyMatrix)

    forestProb should be > grassProb
    forestProb should be <= 1.0
    grassProb should be >= 0.0
  }

  test("defaultBurnDuration returns true after 3 cycles") {
    defaultBurnDuration(5, 8) shouldBe true 
    defaultBurnDuration(5, 7) shouldBe false
  }

  test("defaultRandomProvider returns value between 0.0 and 1.0") {
    val value = defaultRandomProvider()
    value should (be >= 0.0 and be <= 1.0)
  }

