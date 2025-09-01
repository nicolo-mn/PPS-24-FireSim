package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*

enum FireStage(val probabilityFactor: Double, val activationThreshold: Double):

  case Ignition
      extends FireStage(ignitionProbabilityFactor, ignitionActivationThreshold)

  case Active
      extends FireStage(activeProbabilityFactor, activeActivationThreshold)

  case Smoldering extends FireStage(
        smolderingProbabilityFactor,
        smolderingActivationThreshold
      )

object FireStage:

  def nextStage(start: Int, currentCycle: Int, burnDuration: Int): FireStage =
    val ratio = (currentCycle - start).toDouble / burnDuration
    if ratio <= Ignition.activationThreshold then Ignition
    else if ratio <= Active.activationThreshold then Active
    else Smoldering
