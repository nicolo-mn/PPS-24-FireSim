package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*
import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.CellType

type ProbabilityCalc = (CellType, SimParams, Int, Int, Matrix) => Double
type BurnDurationPolicy = (CellType, Int, Int) => Boolean

val defaultBurnDuration: BurnDurationPolicy =
  (cellType, start, current) =>
    (current - start) >= cellType.vegetation.burnDuration

val defaultProbabilityCalc: ProbabilityCalc =
  (cellType, params, r, c, matrix) =>
    if !cellType.isFlammable || cellType.isBurning
    then minProbability
    else

      val humidityFactor = 1.0 / (1.0 + math.exp(
        (params.humidity - humidityMidpoint) / humidityScale
      ))
      val temperatureFactor =
        1.0 / (1.0 + math.exp(
          -(params.temperature - temperatureMidpoint) / temperatureScale
        ))

      val neighborInfluence = neighbors(r, c, matrix)
        .map(pos => matrix(pos._1)(pos._2))
        .collect { case CellType.Burning(_, stage, _) =>
          stage.probabilityFactor
        }
        .sum

      val probability = cellType.vegetation.flammability *
        humidityFactor *
        temperatureFactor *
        (baseNeighborInfluence + neighborInfluence * neighborInfluenceWeight)

      math.min(maxProbability, math.max(minProbability, probability))
