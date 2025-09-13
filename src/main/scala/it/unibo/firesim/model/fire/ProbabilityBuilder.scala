package it.unibo.firesim.model.fire

import scala.language.implicitConversions

/** Each method composes an additional calculation policy on top of the
  * accumulated function.
  *
  * @param currentCalc
  *   The `ProbabilityCalc` function composed so far.
  */
case class ProbabilityDSL(private val currentCalc: ProbabilityCalc):

  /** Adds the wind effect */
  def withWind: ProbabilityDSL =
    copy(currentCalc = directionalWindProbabilityDynamic(currentCalc))

  /** Adds a humidity penalty */
  def withHumidityPenalty: ProbabilityDSL =
    copy(currentCalc = humidityAware(currentCalc))

  /** Returns the final composed `ProbabilityCalc` */
  def build: ProbabilityCalc = currentCalc

object ProbabilityDSL:

  /** The entry point for the DSL. Starts the building process using the
    * `defaultProbabilityCalc` as the base.
    */
  def apply(): ProbabilityDSL =
    ProbabilityDSL(defaultProbabilityCalc)

  /** Automatically converts the builder to its final `ProbabilityCalc` function
    * when it's used in a context that expects one.
    */
  given Conversion[ProbabilityDSL, ProbabilityCalc] = _.build
