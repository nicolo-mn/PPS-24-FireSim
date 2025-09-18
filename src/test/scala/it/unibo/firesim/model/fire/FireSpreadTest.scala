package it.unibo.firesim.model.fire

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.SimParams
import it.unibo.firesim.model.map.CellType.{Grass, Water}
import it.unibo.firesim.model.fire.FireStage.Active
import it.unibo.firesim.model.map.{CellType, Matrix}
import it.unibo.firesim.util.*

class FireSpreadTest extends AnyFlatSpec with Matchers:

  val rng: RNG = SimpleRNG(42)

  given prob: ProbabilityCalc = (_, _, _, _, _) => 1.0

  given burn: BurnDurationPolicy = defaultBurnDuration

  "fireSpread" should "turn flammable cells into burning with max probability and a burning neighbor" in {
    val matrix: Matrix = Vector(
      Vector(
        CellType.Grass,
        CellType.Burning(0, FireStage.Active, CellType.Grass)
      ),
      Vector(CellType.Grass, CellType.Grass)
    )
    val params = SimParams(0, 0, 30, 10)

    val (result, newBurning, _) =
      fireSpread(matrix, Set((0, 1)), params, 1, rng)
    result(0)(0) shouldBe a[CellType.Burning]
    result(0)(0).asInstanceOf[CellType.Burning].originalType shouldBe Grass
    result(1)(0) shouldBe a[CellType.Burning]
  }

  it should "turn burning cells into burnt after after their burn duration" in {
    val testCell = CellType.Burning(0, Active, Grass)
    val matrix: Matrix = Vector(Vector(testCell))
    val params = SimParams(0, 0, 20, 50)

    val testBurnDuration: BurnDurationPolicy = (cellType, start, current) =>
      (current - start) >= 3
    given BurnDurationPolicy = testBurnDuration

    val (midM, _, _) = fireSpread(matrix, Set((0, 0)), params, 2, rng)
    midM(0)(0) shouldBe a[CellType.Burning]
    val (newM, _, _) = fireSpread(matrix, Set((0, 0)), params, 3, rng)
    newM(0)(0) shouldBe CellType.Burnt
  }

  it should "not burn a cell if probability is zero" in {
    val matrix: Matrix = Vector(
      Vector(
        CellType.Grass,
        CellType.Burning(0, FireStage.Active, CellType.Grass)
      )
    )
    val params = SimParams(0, 0, 20, 50)
    given prob: ProbabilityCalc = (_, _, _, _, _) => 0.0

    val (newM, _, _) =
      fireSpread(matrix, Set((0, 1)), params, 1, rng)
    newM(0)(0) shouldBe CellType.Grass
    newM(0)(1) shouldBe a[CellType.Burning]
  }

  it should "be stopped by non-flammable cells" in {
    // G = Grass, B = Burning, W = Water
    // G B G
    // W W W  <- non-flammable
    // G G G
    val matrix: Matrix = Vector(
      Vector(Grass, CellType.Burning(0, Active, Grass), Grass),
      Vector(Water, Water, Water),
      Vector(Grass, Grass, Grass)
    )
    val params = SimParams(10, 10, 40, 10)
    val (result, _, _) = fireSpread(matrix, Set((0, 1)), params, 1, rng)

    // Il fuoco si propaga nella prima riga
    result(0)(0) shouldBe a[CellType.Burning]
    result(0)(2) shouldBe a[CellType.Burning]

    // La barriera d'acqua rimane intatta
    result(1)(0) shouldBe Water
    result(1)(1) shouldBe Water
    result(1)(2) shouldBe Water

    // fire should not
    result(2)(0) shouldBe Grass
    result(2)(1) shouldBe Grass
    result(2)(2) shouldBe Grass
  }
