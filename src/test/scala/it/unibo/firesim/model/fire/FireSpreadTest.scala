package it.unibo.firesim.model.fire

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.SimParams
import it.unibo.firesim.model.map.CellType.Grass
import it.unibo.firesim.model.fire.FireStage.{Active, Ignition, Smoldering}
import it.unibo.firesim.model.map.{CellType, Matrix}
import it.unibo.firesim.util.*

class FireSpreadTest extends AnyFlatSpec with Matchers:

  val rng: RNG = SimpleRNG(42)

  given prob: ProbabilityCalc = (_, _, _, _, _) => 1.0

  given burn: BurnDurationPolicy = defaultBurnDuration

  "fireSpread" should "turn flammable cells into burning with high probability and a burning neighbor" in {
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

  "defaultProbabilityCalc" should "give higher probability for grass than forest" in {
    val dummyMatrix = Vector.empty
    val params = SimParams(0, 0, 30, 20)

    val forestProb =
      defaultProbabilityCalc(CellType.Forest, params, 0, 0, dummyMatrix)
    val grassProb =
      defaultProbabilityCalc(CellType.Grass, params, 0, 1, dummyMatrix)

    grassProb should be > forestProb
    forestProb should be <= 1.0
    grassProb should be >= 0.0
  }

  "humidityAdjusted" should "apply penalty if humidity is high" in {
    val matrix = Vector(Vector(CellType.Burning(0, Ignition, Grass)))
    val lowHumidity = SimParams(1, 0, 30, 30)
    val highHumidity = SimParams(1, 0, 30, 90)

    val base: ProbabilityCalc = (_, _, _, _, _) => 0.4
    val humidityAdjusted = humidityAware(base)

    val low = humidityAdjusted(CellType.Forest, lowHumidity, 0, 0, matrix)
    val high = humidityAdjusted(CellType.Forest, highHumidity, 0, 0, matrix)

    high should be < low
  }

  "directionalWindProbabilityDynamic" should "boost probability only in the wind direction" in {
    val params = SimParams(10, 90, 25, 0)

    val matrix: Matrix = Vector(
      Vector(
        CellType.Grass,
        CellType.Grass,
        CellType.Grass
      ),
      Vector(
        CellType.Grass,
        CellType.Burning(0, Ignition, Grass),
        CellType.Grass
      ),
      Vector(
        CellType.Grass,
        CellType.Grass,
        CellType.Grass
      )
    )

    val base: ProbabilityCalc = (_, _, _, _, _) => 0.4
    val windAdjusted = directionalWindProbabilityDynamic(base)

    val rightCellProb = windAdjusted(matrix(1)(2), params, 1, 2, matrix)
    val leftCellProb = windAdjusted(matrix(1)(0), params, 1, 0, matrix)

    leftCellProb should be > rightCellProb
  }

  "waterHumidityWind" should "apply MAX penalty for water directly upwind " in {
    val matrix: Matrix = Vector(
      Vector(
        CellType.Grass,
        CellType.Grass
      ),
      Vector(CellType.Grass, CellType.Water)
    )
    val params = SimParams(10, 190, 10, 10)

    val base: ProbabilityCalc = (_, _, _, _, _) => 0.4
    val withWater = waterHumidityWind(base)

    val upwindWater = withWater(matrix(0)(1), params, 0, 1, matrix)
    val normal = withWater(matrix(0)(0), params, 0, 0, matrix)
    normal should be > upwindWater
  }

  "FireStage.nextStage" should "correctly transition between fire stages" in {
    val burnDuration = 10
    val startCycle = 0

    FireStage.nextStage(startCycle, 1, burnDuration) shouldBe Ignition

    FireStage.nextStage(startCycle, 4, burnDuration) shouldBe Active

    FireStage.nextStage(startCycle, 7, burnDuration) shouldBe Active

    FireStage.nextStage(startCycle, 9, burnDuration) shouldBe Smoldering
    FireStage.nextStage(startCycle, 10, burnDuration) shouldBe Smoldering
  }

  "fromAngle (Wind)" should "correctly convert angle to WindDirection" in {
    val angleToDirection = Map(
      0.0 -> WindDirection.North,
      45.0 -> WindDirection.NorthEast,
      90.0 -> WindDirection.East,
      135.0 -> WindDirection.SouthEast,
      180.0 -> WindDirection.South,
      225.0 -> WindDirection.SouthWest,
      270.0 -> WindDirection.West,
      315.0 -> WindDirection.NorthWest,
      360.0 -> WindDirection.North
    )

    angleToDirection.foreach { (angle, direction) =>
      fromAngle(angle) shouldBe direction
    }
  }

  "ProbabilityBuilder" should "compose policies correctly" in {
    val params = SimParams(
      windSpeed = 10,
      windAngle = 90,
      temperature = 25,
      humidity = 95
    )
    val matrix =
      Vector(Vector(CellType.Grass, CellType.Burning(0, Active, Grass)))

    val baseProbValue = 0.5
    val baseCalc: ProbabilityCalc = (_, _, _, _, _) => baseProbValue

    val probWithWind = ProbabilityBuilder(baseCalc).withWind.build
    val probWithHumidity =
      ProbabilityBuilder(baseCalc).withHumidityPenalty.build
    val probComposed =
      ProbabilityBuilder(baseCalc).withWind.withHumidityPenalty.build

    val windRes = probWithWind(CellType.Grass, params, 0, 0, matrix)
    val humidityRes = probWithHumidity(CellType.Grass, params, 0, 0, matrix)
    val composedRes = probComposed(CellType.Grass, params, 0, 0, matrix)

    windRes should be > baseProbValue
    humidityRes should be < baseProbValue
    composedRes should be < windRes
    composedRes should not be baseProbValue
  }
