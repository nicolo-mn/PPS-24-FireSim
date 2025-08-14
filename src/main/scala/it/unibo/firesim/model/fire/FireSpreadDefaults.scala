package it.unibo.firesim.model.fire

import it.unibo.firesim.model.cell.CellType
import it.unibo.firesim.model.fire.{ProbabilityCalc, BurnDurationPolicy, RandomProvider}

private def baseFlammability(cellType: CellType): Double =
  cellType match
    case CellType.Forest => 0.6
    case CellType.Grass  => 0.3
    case _               => 0.0

object WindyHumidDefaults:

  given ProbabilityCalc =
    humidityAware(directionalWindProbabilityDynamic(defaultProbabilityCalc))

  given BurnDurationPolicy = defaultBurnDuration

  given RandomProvider = defaultRandomProvider

val defaultProbabilityCalc: ProbabilityCalc =
  (cellType, params, r, c, matrix) =>
    val base = baseFlammability(cellType)
    val humidityFactor = math.max(0.0, 1.0 - params.humidity / 100.0)
    val temperatureFactor = math.max(0.0, (params.temperature - 15.0) / 25.0)
    val p = base * humidityFactor * temperatureFactor
    math.max(0.0, math.min(p, 1.0))

val defaultBurnDuration: BurnDurationPolicy = (start, current) =>
  (current - start) >= 100

val defaultRandomProvider: RandomProvider = () =>
  scala.util.Random.nextDouble()
