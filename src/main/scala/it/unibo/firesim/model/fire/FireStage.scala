package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*

/** Represents the different stages of a fire lifecycle
  *
  * @param probabilityFactor
  *   multiplier applied to the ignition probability for neighboring cells
  *   during this stage.
  * @param threshold
  *   fraction (0.0–1.0) of the burn duration at which the stage transitions.
  */
enum FireStage(val probabilityFactor: Double, val threshold: Double):

  /** Initial stage of a fire: newly ignited, moderate probability factor.
    */
  case Ignition
      extends FireStage(ignitionProbabilityFactor, ignitionActivationThreshold)

  /** Peak fire activity: highest probability factor, main spread phase.
    */
  case Active
      extends FireStage(activeProbabilityFactor, activeActivationThreshold)

  /** Final stage: reduced intensity, contributes little to spread.
    */
  case Smoldering extends FireStage(
        smolderingProbabilityFactor,
        smolderingActivationThreshold
      )

object FireStage:

  /** Determines the current fire stage based on the elapsed burn time
    * @param start
    *   the cycle when the cell started burning
    * @param currentCycle
    *   the current simulation cycle
    * @param burnDuration
    *   the total configured burn duration for the cell
    * @return
    *   the corresponding [[FireStage]]
    */
  def nextStage(start: Int, currentCycle: Int, burnDuration: Int): FireStage =
    val ratio = (currentCycle - start).toDouble / burnDuration
    ratio match
      case r if r <= Ignition.threshold => Ignition
      case r if r <= Active.threshold   => Active
      case _                            => Smoldering
