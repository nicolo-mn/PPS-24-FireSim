package it.unibo.firesim.model.fire

import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.CellType

type ProbabilityCalc = (CellType, SimParams, Int, Int, Matrix) => Double
type BurnDurationPolicy = (CellType, Int, Int) => Boolean

val defaultBurnDuration: BurnDurationPolicy =
  (cellType, start, current) =>
    (current - start) >= Vegetation.burnDuration(
      CellTypeOps.vegetation(cellType)
    )

val defaultProbabilityCalc: ProbabilityCalc =
  (cellType, params, r, c, matrix) =>
    if !CellTypeOps.isFlammable(cellType) then 0.0
    else
      val veg = CellTypeOps.vegetation(cellType)
      val stageFactor = cellType match
        case CellType.Burning(_, stage, _) => FireStage.stageProbFactor(stage)
        case _                             => 1.0
      val humidityFactor = math.max(0.0, 1.0 - params.humidity / 100.0)
      val temperatureFactor = math.max(0.0, (params.temperature - 15.0) / 25.0)

      val p = Vegetation.flammability(veg) *
        stageFactor *
        humidityFactor *
        temperatureFactor

      math.min(1.0, math.max(0.0, p))
