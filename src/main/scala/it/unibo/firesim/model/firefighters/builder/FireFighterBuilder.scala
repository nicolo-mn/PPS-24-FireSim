package it.unibo.firesim.model.firefighters.builder

import it.unibo.firesim.model.firefighters.{DistanceStrategy, BresenhamMovement, FireFighter}

class FireFighterBuilder:
  private var actionableCells = Seq.empty[(Int, Int)]
  private var station = Option.empty[(Int, Int)]

  def withRay(ray: Int): Unit =
    require(ray >= 0, "Cannot create a firefighter with a negative action ray!")
    actionableCells = cellsInRay(ray)

  def stationedIn(s: (Int, Int)): Unit =
    require(
      s._1 >= 0 && s._2 >= 0,
      "Cannot station a firefighter in a negative cell!"
    )
    station = Some(s)

  def build(): FireFighter =
    require(
      actionableCells.nonEmpty && station.nonEmpty,
      "Firefighters require a base station and ray of action!"
    )
    val s = station.get
    FireFighter(
      s,
      actionableCells,
      s,
      s,
      true,
      BresenhamMovement(s, s, 0, 0, 0, 0, 0),
      DistanceStrategy.euclideanDistance
    )

  private def cellsInRay(ray: Int): Seq[(Int, Int)] =
    for
      dr <- -ray to ray
      dc <- -ray to ray
    yield (dr, dc)
