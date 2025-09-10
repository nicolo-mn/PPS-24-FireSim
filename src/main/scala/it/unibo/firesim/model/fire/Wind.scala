package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*
import it.unibo.firesim.model.inBounds

/** Boosts the ignition probability for cells that are downwind from a burning
  * neighbor.
  * @param base
  *   The base `ProbabilityCalc` function to decorate.
  * @return
  *   A new `ProbabilityCalc` function that includes the wind effect.
  */
def directionalWindProbabilityDynamic(base: ProbabilityCalc): ProbabilityCalc =
  (cell, params, r, c, matrix) =>
    val dir = fromAngle(params.windAngle)
    val rr = r + dir.dr
    val cc = c + dir.dc

    val neighborIsBurning =
      matrix.inBounds(rr, cc) && matrix(rr)(cc).isBurning

    val speedFactor = baseWindBoost + math.tanh(
      params.windSpeed / windNormalization
    ) * maxWindBoost
    val windBoost = if neighborIsBurning then speedFactor else baseWindBoost
    val baseProb = base(cell, params, r, c, matrix)
    math.min(baseProb * windBoost, maxProbability)

/** A `ProbabilityCalc` to add a high humidity penalty.
  * @param base
  *   The base `ProbabilityCalc` function to decorate.
  * @return
  *   A new `ProbabilityCalc` function that includes the humidity penalty.
  */
def humidityAware(base: ProbabilityCalc): ProbabilityCalc =
  (cell, params, r, c, matrix) =>
    val penalty =
      if params.humidity > highHumidity then humidityPenalty else maxProbability
    base(cell, params, r, c, matrix) * penalty

enum WindDirection(val dr: Int, val dc: Int):
  case North extends WindDirection(-1, 0)
  case NorthEast extends WindDirection(-1, -1)
  case East extends WindDirection(0, -1)
  case SouthEast extends WindDirection(1, -1)
  case South extends WindDirection(1, 0)
  case SouthWest extends WindDirection(1, 1)
  case West extends WindDirection(0, 1)
  case NorthWest extends WindDirection(-1, 1)

private def fromAngle(angle: Double): WindDirection =
  val numDirections = WindDirection.values.length
  val sectorSize = 360.0 / numDirections
  WindDirection.values(((angle % 360) / sectorSize).toInt % numDirections)
