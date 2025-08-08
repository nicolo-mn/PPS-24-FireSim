package it.unibo.firesim.model.fire

import it.unibo.firesim.model.inBounds
import it.unibo.firesim.model.cell.CellType

given windAndHumidityAdjusted: ProbabilityCalc =
humidityAware(directionalWindProbabilityDynamic(defaultProbabilityCalc))

def directionalWindProbabilityDynamic(base: ProbabilityCalc): ProbabilityCalc =
  (cell, params, r, c, matrix) =>
    val dir = fromAngle(params.windAngle)
    val rr = r + dir.dr
    val cc = c + dir.dc

    val windBoost =
      if matrix.inBounds(rr, cc) && matrix(rr)(cc).cellType.isInstanceOf[CellType.Burning]
      then 1.5 else 1.0

    val baseProb = base(cell, params, r, c, matrix)
    math.min(baseProb * windBoost, 1.0)

def humidityAware(base: ProbabilityCalc): ProbabilityCalc =
  (cell, params, r, c, matrix) =>
    val penalty = if params.humidity > 80 then 0.7 else 1.0
    base(cell, params, r, c, matrix) * penalty

enum WindDirection(val dr: Int, val dc: Int):
  case East extends WindDirection(0, 1)
  case NorthEast extends WindDirection(-1, 1)
  case North extends WindDirection(-1, 0)
  case NorthWest extends WindDirection(-1, -1)
  case West extends WindDirection(0, -1)
  case SouthWest extends WindDirection(1, -1)
  case South extends WindDirection(1, 0)
  case SouthEast extends WindDirection(1, 1)
  
private def fromAngle(angle: Double): WindDirection =
  val normalized = ((angle + 22.5) % 360).toInt / 45
  WindDirection.values(normalized)
