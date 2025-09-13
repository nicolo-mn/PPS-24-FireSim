package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*
import it.unibo.firesim.model.inBounds

import scala.annotation.tailrec

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

  /** reduces ignition probability for cells receiving humid wind from a body of
    * water, with an effect that diminishes with distance.
    *
    * @param base
    *   The base `ProbabilityCalc` function to decorate
    * @return
    *   A new `ProbabilityCalc` that includes the extended coastal humidity
    *   effect.
    */

def waterHumidityWind(base: ProbabilityCalc): ProbabilityCalc =
  (cell, params, r, c, matrix) =>

    val baseProb = base(cell, params, r, c, matrix)
    val windOrigin = fromAngle(params.windAngle)

    /** A tail-recursive helper function that searches for a water cell upwind.
      *
      * @param currentR
      *   current row in the search.
      * @param currentC
      *   current column in the search.
      * @param distance
      *   the distance from the original cell.
      * @return
      *   if the water is found within the search range
      */
    @tailrec
    def findWaterUpwind(
        currentR: Int,
        currentC: Int,
        distance: Int
    ): Option[Int] =
      if !matrix.inBounds(
          currentR,
          currentC
        ) || distance > coastalEffectMaxRange
      then
        None
      else if matrix(currentR)(currentC).isWater then
        Some(distance)
      else
        findWaterUpwind(
          currentR - windOrigin.dr,
          currentC - windOrigin.dc,
          distance + 1
        )

    val upwindR = r - windOrigin.dr
    val upwindC = c - windOrigin.dc
    val waterDistance = findWaterUpwind(upwindR, upwindC, 1)

    waterDistance match
      case Some(d) =>
        val falloffFactor =
          (coastalEffectMaxRange - d + 1).toDouble / coastalEffectMaxRange
        val penaltyMultiplier = 1.0 - (maxCoastalHumidityEffect * falloffFactor)
        math.max(0, baseProb * penaltyMultiplier)
      case None =>
        baseProb

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
