package it.unibo.firesim.model.fire

import it.unibo.firesim.model.cell.CellType

enum FireStage:
  case Ignition
  case Active
  case Smoldering

object FireStage:
  def stageProbFactor(stage: FireStage): Double = stage match
    case Ignition   => 0.8
    case Active     => 1.0
    case Smoldering => 0.3

  private def threshold(stage: FireStage): Double = stage match
    case Ignition   => 0.1
    case Active     => 0.8
    case Smoldering => 1.0

  def nextStage(start: Int, currentCycle: Int, burnDuration: Int): FireStage =
    val ratio = (currentCycle - start).toDouble / burnDuration
    if ratio <= threshold(Ignition)   then Ignition
    else if ratio <= threshold(Active) then Active
    else Smoldering

object CellTypeOps:
  def vegetation(cell: CellType): Vegetation = cell match
    case CellType.Forest      => Vegetation.Forest
    case CellType.Grass       => Vegetation.Grass
    case _                    => Vegetation.None

  def isFlammable(cell: CellType): Boolean =
    Vegetation.flammability(vegetation(cell)) > 0
   
  def isBurning(cell: CellType): Boolean = cell match
    case CellType.Burning(_, _, _) => true
    case _ => false
