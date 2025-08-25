package it.unibo.firesim.model.fire

enum Vegetation:
  case Forest
  case Grass
  case None

object Vegetation:
  def flammability(v: Vegetation): Double = v match
    case Forest => 0.2
    case Grass  => 0.1
    case None   => 0.0

  def burnDuration(v: Vegetation): Int = v match
    case Forest => 100
    case Grass  => 70
    case None   => 0
