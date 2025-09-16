package it.unibo.firesim.model.firefighters.builder

import it.unibo.firesim.model.firefighters.{FireFighter, MoveStrategy}

/** Builder for constructing a FireFighter instance.
  *
  * Provides methods to configure a firefighter's base station and action ray.
  */
class FireFighterBuilder:
  private var neighborsInRay = Seq.empty[(Int, Int)]
  private var station = Option.empty[(Int, Int)]

  /** Sets the action ray for the firefighter.
    *
    * @param ray
    *   the range of action; must be non-negative.
    * @throws IllegalArgumentException
    *   if the ray is negative.
    */
  def withRay(ray: Int): Unit =
    require(ray >= 0, "Cannot create a firefighter with a negative action ray!")
    neighborsInRay = cellsInRay(ray)

  /** Sets the base station for the firefighter.
    *
    * @param s
    *   the coordinates for the station; both values must be non-negative.
    * @throws IllegalArgumentException
    *   if any coordinate is negative.
    */
  def stationedIn(s: (Int, Int)): Unit =
    require(
      s._1 >= 0 && s._2 >= 0,
      "Cannot station a firefighter in a negative cell!"
    )
    station = Some(s)

  /** Builds and returns a FireFighter instance.
    *
    * @return
    *   a configured FireFighter.
    * @throws IllegalArgumentException
    *   if the action ray or station is not set.
    */
  def build(): FireFighter =
    require(
      neighborsInRay.nonEmpty && station.nonEmpty,
      "Firefighters require a base station and ray of action!"
    )
    val s = station.get
    FireFighter(
      s,
      neighborsInRay.toSet,
      s,
      s,
      true,
      LazyList.continually(s),
      MoveStrategy.bresenham
    )

  private def cellsInRay(ray: Int): Seq[(Int, Int)] =
    for
      dr <- -ray to ray
      dc <- -ray to ray
    yield (dr, dc)
