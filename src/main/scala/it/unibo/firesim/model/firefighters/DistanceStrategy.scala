package it.unibo.firesim.model.firefighters

trait DistanceStrategy:
  def distance(p1: (Int, Int), p2: (Int, Int)): Int

class ChebyshevDistance extends DistanceStrategy:

  override def distance(p1: (Int, Int), p2: (Int, Int)): Int =
    math.max(math.abs(p1._1 - p2._1), math.abs(p1._2 - p2._2))
