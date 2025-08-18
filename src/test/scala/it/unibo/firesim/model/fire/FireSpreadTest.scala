package it.unibo.firesim.model.fire

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.CellType
import it.unibo.firesim.model.fire.WindyHumidDefaults.given

class FireSpreadTest extends AnyFlatSpec with Matchers:

  "fireSpread" should "turn flammable cells into burning with high probability and a burning neighbor" in {
    val matrix: Matrix = Vector(
      Vector(CellType.Grass, CellType.Burning(0)),
      Vector(CellType.Grass, CellType.Grass)
    )
    val params = SimParams(0, 0, 30, 10)

    given prob: ProbabilityCalc = (_, _, _, _, _) => 1.0
    given burn: BurnDurationPolicy = (_, _) => false
    given rand: RandomProvider = () => 0.0

    val result = fireSpread(matrix, params, 1)
    result(0)(0) shouldBe CellType.Burning(1)
    result(1)(0) shouldBe CellType.Burning(1)
  }

  it should "turn burning cells into burnt after 3 cycles" in {
    val matrix: Matrix = Vector(Vector(CellType.Burning(0)))
    val params = SimParams(0, 0, 20, 50)

    given prob: ProbabilityCalc = (_, _, _, _, _) => 0.0
    given burn: BurnDurationPolicy = (start, current) => (current - start) >= 3
    given rand: RandomProvider = () => 1.0

    val newM = fireSpread(matrix, params, 3)(using prob, burn, rand)
    newM(0)(0) shouldBe CellType.Burnt
  }

  it should "not burn a cell if probability is zero" in {
    val matrix: Matrix = Vector(
      Vector(CellType.Grass, CellType.Burning(0))
    )
    val params = SimParams(0, 0, 20, 50)

    given prob: ProbabilityCalc = (_, _, _, _, _) => 0.0
    given burn: BurnDurationPolicy = (_, _) => false
    given rand: RandomProvider = () => 0.0

    val newM = fireSpread(matrix, params, 1)(using prob, burn, rand)
    newM(0)(0) shouldBe CellType.Grass
  }

  "defaultProbabilityCalc" should "give higher probability for forest than grass" in {
    val dummyMatrix = Vector.empty
    val params = SimParams(0, 0, 30, 20)

    val forestProb =
      defaultProbabilityCalc(CellType.Forest, params, 0, 0, dummyMatrix)
    val grassProb =
      defaultProbabilityCalc(CellType.Grass, params, 0, 1, dummyMatrix)

    forestProb should be > grassProb
    forestProb should be <= 1.0
    grassProb should be >= 0.0
  }

  "defaultRandomProvider" should "return a value between 0.0 and 1.0" in {
    val value = defaultRandomProvider()
    value should (be >= 0.0 and be <= 1.0)
  }

  "windAndHumidityAdjusted" should "apply penalty if humidity is high" in {
    val matrix = Vector(Vector(CellType.Burning(0)))
    val lowHumidity = SimParams(1, 0, 30, 30)
    val highHumidity = SimParams(1, 0, 30, 90)

    val p = summon[ProbabilityCalc]
    val low = p(CellType.Forest, lowHumidity, 0, 0, matrix)
    val high = p(CellType.Forest, highHumidity, 0, 0, matrix)

    high should be < low
  }

  "directionalWindProbabilityDynamic" should "boost probability if cell in wind direction is burning" in {
    val params = SimParams(1, 0, 25, 0)
    val matrix: Matrix = Vector(
      Vector(
        CellType.Grass,
        CellType.Grass,
        CellType.Grass
      ),
      Vector(
        CellType.Grass,
        CellType.Grass,
        CellType.Burning(0)
      ),
      Vector(
        CellType.Grass,
        CellType.Grass,
        CellType.Grass
      )
    )
    val base: ProbabilityCalc = (_, _, _, _, _) => 0.4
    val windAdjusted = directionalWindProbabilityDynamic(base)

    val boosted = windAdjusted(matrix(1)(1), params, 1, 1, matrix)

    boosted should be > 0.4
    boosted should be <= 1.0
  }
