package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*
import it.unibo.firesim.model.map.{CellType, Matrix, Position}
import it.unibo.firesim.model.SimParams

/** A function type representing the probability that a given cell will ignite
  * during a simulation cycle
  *
  * @param cellType
  *   the type of the cell to evaluate
  * @param params
  *   the current global simulation parameters
  * @param pos
  *   position of the cell in the matrix
  * @param matrix
  *   the full simulation grid
  * @return
  *   a probability value in the range `[minProbability, maxProbability]`
  */
type ProbabilityCalc = (CellType, SimParams, Position, Matrix) => Double

/** The policy that determines whether a burning cell has exceeded its burn
  * duration and should transition to a burnt state.
  *
  * @param cellType
  *   the type of the cell
  * @param start
  *   the cycle at which the cell started burning
  * @param current
  *   the current simulation cycle
  * @return
  *   true if the cell has finished burning, false otherwise
  */
type BurnDurationPolicy = (CellType, Int, Int) => Boolean

/** Default burn duration policy: a cell stops burning when the number of
  * elapsed cycles is greater than its vegetation's configured `burnDuration`.
  */
val defaultBurnDuration: BurnDurationPolicy =
  (cellType, start, current) =>
    (current - start) >= cellType.vegetation.burnDuration

/** Default probability calculation strategy for ignition. Uses only
  * temperature, humidity and neighbour influence
  */
val defaultProbabilityCalc: ProbabilityCalc =
  (cellType, params, pos, matrix) =>
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

      val neighborInfluence = neighbors(pos, matrix)
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
