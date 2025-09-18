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

  given prob: ProbabilityCalc = (_, _, _, _) => 1.0

  given burn: BurnDurationPolicy = defaultBurnDuration

  "fireSpread" should "ignite adjacent flammable cells when probability = 1" in {
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

  it should "turn burning cells to burnt after their burn duration" in {
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
    given prob: ProbabilityCalc = (_, _, _, _) => 0.0

    val (newM, _, _) =
      fireSpread(matrix, Set((0, 1)), params, 1, rng)
    newM(0)(0) shouldBe CellType.Grass
    newM(0)(1) shouldBe a[CellType.Burning]
  }

  "fire" should "be stopped by non-flammable cells" in {
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

    result(0)(0) shouldBe a[CellType.Burning]
    result(0)(2) shouldBe a[CellType.Burning]

    // water should not become burning
    result(1)(0) shouldBe Water
    result(1)(1) shouldBe Water
    result(1)(2) shouldBe Water

    // fire should not cross a non-flammable cells
    result(2)(0) shouldBe Grass
    result(2)(1) shouldBe Grass
    result(2)(2) shouldBe Grass
  }

  "fireSpread" should "not reignite burnt cells" in {
    val matrix: Matrix = Vector(
      Vector(CellType.Burnt),
      Vector(CellType.Burning(0, FireStage.Active, CellType.Grass))
    )
    val params = SimParams(0, 0, 30, 10)

    val (result, _, _) = fireSpread(matrix, Set((1, 0)), params, 1, rng)
    result(0)(0) shouldBe CellType.Burnt
  }

  it should "transition a burning cell through fire stages over time" in {
    val grassBurning = CellType.Burning(0, FireStage.Ignition, CellType.Grass)
    val matrix: Matrix = Vector(Vector(grassBurning))
    val params = SimParams(0, 0, 30, 10)
    val grassBurnDuration = Grass.vegetation.burnDuration
    // Ignition -> Active
    val (matrixAfter4Cycles, _, _) = fireSpread(
      matrix,
      Set((0, 0)),
      params,
      (grassBurnDuration * FireStage.Ignition.threshold).toInt + 1,
      rng
    )
    val cellAfter4Cycles =
      matrixAfter4Cycles(0)(0).asInstanceOf[CellType.Burning]
    cellAfter4Cycles.fireStage shouldBe FireStage.Active

    // Active -> Smoldering
    val (matrixAfter9Cycles, _, _) = fireSpread(
      matrix,
      Set((0, 0)),
      params,
      (grassBurnDuration * FireStage.Active.threshold).toInt + 1,
      rng
    )
    val cellAfter9Cycles =
      matrixAfter9Cycles(0)(0).asInstanceOf[CellType.Burning]
    cellAfter9Cycles.fireStage shouldBe FireStage.Smoldering
  }

  it should "ignore positions in burning set that are not actually burning" in {
    val matrix: Matrix = Vector(Vector(CellType.Grass))
    val params = SimParams(0, 0, 30, 10)

    // pass a grass inside the burn set
    val (result, _, _) = fireSpread(matrix, Set((0, 0)), params, 1, rng)
    result(0)(0) shouldBe CellType.Grass
  }

  it should "keep a burning cell unchanged if it has not reached the next stage yet" in {
    val burningGrass = CellType.Burning(0, FireStage.Ignition, CellType.Grass)
    val matrix: Matrix = Vector(Vector(burningGrass))
    val params = SimParams(0, 0, 30, 10)

    // currentCycle too low to change state
    val (result, _, _) = fireSpread(matrix, Set((0, 0)), params, 1, rng)
    result(0)(0) shouldBe burningGrass
  }
