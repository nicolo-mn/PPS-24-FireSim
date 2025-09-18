package it.unibo.firesim.model.fire

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.SimParams
import it.unibo.firesim.model.map.CellType
import it.unibo.firesim.model.map.CellType.Grass
import it.unibo.firesim.model.fire.FireStage.Active

class ProbabilityBuilderTest extends AnyFlatSpec with Matchers:

  it should "compose policies correctly" in {
    val params =
      SimParams(windSpeed = 10, windAngle = 90, temperature = 25, humidity = 95)
    val matrix = Vector(Vector(Grass, CellType.Burning(0, Active, Grass)))
    val baseProbValue = 0.5
    val baseCalc: ProbabilityCalc = (_, _, _, _) => baseProbValue

    val probWithWind = ProbabilityBuilder(baseCalc).withWind.build
    val probWithHumidity =
      ProbabilityBuilder(baseCalc).withHumidityPenalty.build
    val probComposed =
      ProbabilityBuilder(baseCalc).withWind.withHumidityPenalty.build

    val windRes = probWithWind(Grass, params, (0, 0), matrix)
    val humidityRes = probWithHumidity(Grass, params, (0, 0), matrix)
    val composedRes = probComposed(Grass, params, (0, 0), matrix)

    windRes should be > baseProbValue
    humidityRes should be < baseProbValue
    composedRes should be < windRes
    composedRes should not be baseProbValue
  }

  it should "correctly compose probability functions" in {
    val builder = ProbabilityBuilder().withWind.withWaterEffects
    val composedFunc = builder

    // Manually compose for comparison
    val manualComposedFunc = waterHumidityWind(
      directionalWindProbabilityDynamic(defaultProbabilityCalc)
    )

    val matrix = Vector(
      Vector(
        CellType.Water,
        CellType.Burning(0, FireStage.Active, CellType.Grass)
      )
    )
    val params =
      SimParams(windSpeed = 50, windAngle = 90, temperature = 30, humidity = 30)

    val probBuilder = composedFunc(CellType.Grass, params, (0, 0), matrix)
    val probManual = manualComposedFunc(CellType.Grass, params, (0, 0), matrix)

    probBuilder shouldBe probManual
  }
