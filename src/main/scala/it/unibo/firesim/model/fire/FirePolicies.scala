package it.unibo.firesim.model.fire

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
    then 0.0
    else

      val humidityFactor = 1.0 / (1.0 + math.exp((params.humidity - 70) / 20.0))
      val temperatureFactor =
        1.0 / (1.0 + math.exp(-(params.temperature - 20) / 5.0))

      val neighborInfluence = neighbors(r, c, matrix)
        .map(pos => matrix(pos._1)(pos._2))
        .collect { case CellType.Burning(_, stage, _) =>
          stage.probabilityFactor
        }
        .sum

      val p = cellType.vegetation.flammability *
        humidityFactor *
        temperatureFactor *
        (1.0 + neighborInfluence * 0.05)

      math.min(1.0, math.max(0.0, p))
