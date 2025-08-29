package it.unibo.firesim.model.fire

enum FireStage(val probabilityFactor: Double, val activationThreshold: Double):
  case Ignition extends FireStage(0.3, 0.3)
  case Active extends FireStage(1.0, 0.7)
  case Smoldering extends FireStage(0.2, 1.0)

object FireStage:

  def nextStage(start: Int, currentCycle: Int, burnDuration: Int): FireStage =
    val ratio = (currentCycle - start).toDouble / burnDuration
    if ratio <= Ignition.activationThreshold then Ignition
    else if ratio <= Active.activationThreshold then Active
    else Smoldering
