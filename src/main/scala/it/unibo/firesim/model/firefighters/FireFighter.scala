package it.unibo.firesim.model.firefighters

import it.unibo.firesim.model.monads.ReaderStates.ReaderState

object FireFighterState:

  private type CellsOnFire = Set[(Int, Int)]

  def moveStep: ReaderState[CellsOnFire, FireFighter, Unit] =
    ReaderState[CellsOnFire, FireFighter, Unit]((fireCells, f) =>
      val newTarget = if f.loaded && fireCells.nonEmpty then
        val tmp =
          fireCells.minBy(c => f.distanceStrategy.distance(f.position, c))
        if !fireCells.contains(f.target) ||
          tmp == f.target ||
          f.position == f.station ||
          f.distanceStrategy.distance(
            tmp,
            f.station
          ) < f.distanceStrategy.distance(f.target, f.station) / 2 ||
          f.distanceStrategy.distance(
            f.target,
            tmp
          ) < f.distanceStrategy.distance(f.target, f.station) / 2
        then
          tmp
        else f.target
      else
        f.station
      val updated = if f.target != newTarget then
        f.copy(
          moveStrategy = f.moveStrategy.init(f.position, newTarget),
          target = newTarget
        )
      else f
      val (nextPos, strategy) = updated.moveStrategy.move()
      (updated.copy(moveStrategy = strategy, position = nextPos), ())
    )

  def extinguishStep
      : ReaderState[CellsOnFire, FireFighter, CellsOnFire] =
    ReaderState[CellsOnFire, FireFighter, CellsOnFire]((fireCells, f) =>
      if f.loaded && fireCells.contains(f.position) && f.position == f.target
      then
        (
          f.copy(loaded = false),
          f.actionableCells.map(d =>
            (d._1 + f.position._1, d._2 + f.position._2)
          ).filter(c => fireCells.contains(c)).toSet
        )
      else if !f.loaded && f.position == f.station then
        (f.copy(loaded = true), Set.empty[(Int, Int)])
      else
        (f, Set.empty[(Int, Int)])
    )

/** Firefighter unit that extinguishes fire cells, returning to its fire station
  * after every operation to reload
  */
case class FireFighter(
    station: (Int, Int),
    actionableCells: Seq[(Int, Int)],
    position: (Int, Int),
    target: (Int, Int),
    loaded: Boolean,
    moveStrategy: MoveStrategy,
    distanceStrategy: DistanceStrategy
)
