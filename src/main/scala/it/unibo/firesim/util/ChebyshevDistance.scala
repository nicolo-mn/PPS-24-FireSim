package it.unibo.firesim.util

/** Utility object to compute the Chebyshev distance */
object ChebyshevDistance:

  /** Calculates the Chebyshev distance between two points.
    *
    * @param p1
    *   the first point represented as a tuple of (Int, Int)
    * @param p2
    *   the second point represented as a tuple of (Int, Int)
    * @return
    *   the Chebyshev distance between the points
    */
  def distance(p1: (Int, Int), p2: (Int, Int)): Int =
    math.max(math.abs(p1._1 - p2._1), math.abs(p1._2 - p2._2))
