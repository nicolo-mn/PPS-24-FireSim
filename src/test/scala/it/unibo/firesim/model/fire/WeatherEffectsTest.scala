package it.unibo.firesim.model.fire

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.SimParams
import it.unibo.firesim.model.map.CellType.{Grass, Water}
import it.unibo.firesim.model.fire.FireStage.Ignition
import it.unibo.firesim.model.map.{CellType, Matrix}

class WeatherEffectsTest extends AnyFlatSpec with Matchers:

  val baseCalc: ProbabilityCalc = (_, _, _, _, _) => 0.5

  it should "apply a penalty if humidity is high" in {
    val matrix = Vector(Vector(CellType.Burning(0, Ignition, Grass)))
    val lowHumidityParams = SimParams(1, 0, 30, 30)
    val highHumidityParams = SimParams(1, 0, 30, 90)
    val humidityAdjusted = humidityAware(baseCalc)

    val lowProb =
      humidityAdjusted(CellType.Forest, lowHumidityParams, 0, 0, matrix)
    val highProb =
      humidityAdjusted(CellType.Forest, highHumidityParams, 0, 0, matrix)

    highProb should be < lowProb
  }

  it should "boost probability only for cells downwind from a fire" in {
    val params = SimParams(
      windSpeed = 10,
      windAngle = 270,
      temperature = 25,
      humidity = 0
    ) // Wind from West to East
    val matrix: Matrix = Vector(
      Vector(Grass, Grass, Grass),
      Vector(Grass, CellType.Burning(0, Ignition, Grass), Grass),
      Vector(Grass, Grass, Grass)
    )
    val windAdjusted = directionalWindProbabilityDynamic(baseCalc)

    // (1, 2) is to the East (downwind), (1, 0) is to the West (upwind)
    val downwindProb = windAdjusted(matrix(1)(2), params, 1, 2, matrix)
    val upwindProb = windAdjusted(matrix(1)(0), params, 1, 0, matrix)

    downwindProb should be > upwindProb
    upwindProb shouldBe baseCalc(
      matrix(1)(0),
      params,
      1,
      0,
      matrix
    ) // No boost upwind
  }

  it should "apply a penalty for water directly upwind" in {
    val matrix: Matrix = Vector(
      Vector(Grass, Grass),
      Vector(Grass, Water)
    )
    // Wind from South-West (225 deg), so (1,1) is upwind of (0,0)
    val params =
      SimParams(
        windSpeed = 10,
        windAngle = 135,
        temperature = 10,
        humidity = 10
      )
    val withWater = waterHumidityWind(baseCalc)

    val cellWithUpwindWaterProb = withWater(matrix(0)(0), params, 0, 0, matrix)
    val normalCellProb = withWater(matrix(1)(0), params, 1, 0, matrix)

    cellWithUpwindWaterProb should be < normalCellProb
  }
