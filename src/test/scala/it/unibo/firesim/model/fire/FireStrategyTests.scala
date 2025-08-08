package it.unibo.firesim.model.fire

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.{Cell, CellType}

class FireStrategyTests extends AnyFunSuite with Matchers:

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
