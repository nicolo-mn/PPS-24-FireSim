package it.unibo.firesim.model.firefighters

object DistanceStrategy:

  def euclideanDistance(p1: (Int, Int), p2: (Int, Int)): Double =
    val xDiff = p1._1 - p2._1
    val yDiff = p1._2 - p2._2
    math.sqrt(xDiff * xDiff + yDiff * yDiff)
