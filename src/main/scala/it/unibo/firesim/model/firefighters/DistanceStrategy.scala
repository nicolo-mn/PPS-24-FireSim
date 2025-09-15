package it.unibo.firesim.model.firefighters

/** Provides various strategies for calculating distances between two points.
  */
object DistanceStrategy:

  /** Calculates the Euclidean distance between two points.
    *
    * @param p1
    *   the first point represented as a tuple of (Int, Int)
    * @param p2
    *   the second point represented as a tuple of (Int, Int)
    * @return
    *   the Euclidean distance as a Double
    */
  def euclideanDistance(p1: (Int, Int), p2: (Int, Int)): Double =
    val xDiff = p1._1 - p2._1
    val yDiff = p1._2 - p2._2
    math.sqrt(xDiff * xDiff + yDiff * yDiff)
