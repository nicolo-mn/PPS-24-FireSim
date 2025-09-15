package it.unibo.firesim.model.firefighters

import it.unibo.firesim.model.monads.ReaderStates.ReaderState
import it.unibo.firesim.model.firefighters.FireFighterUtils.*
import it.unibo.firesim.model.firefighters.FireFighterUtils.FireFighterAction.*

object FireFighterState:

  private type CellsOnFire = Set[(Int, Int)]

  def moveStep: ReaderState[CellsOnFire, FireFighter, Unit] =
    ReaderState[CellsOnFire, FireFighter, Unit]((fireCells, f) =>
      val newTarget = if f.loaded && fireCells.nonEmpty then
        val tmp =
          fireCells.minBy(c => f.distance(f.position, c))
        if !fireCells.contains(f.target) ||
          tmp == f.target ||
          f.position == f.station ||
          f.distance(
            tmp,
            f.station
          ) < f.distance(f.target, f.station) / 2 ||
          f.distance(
            f.target,
            tmp
          ) < f.distance(f.target, f.station) / 2
        then
          tmp
        else f.target
      else
        f.station
      (f.when(_.target != newTarget)(_ changeTargetTo newTarget).move, ())
    )

  def extinguishStep
      : ReaderState[CellsOnFire, FireFighter, CellsOnFire] =
    ReaderState[CellsOnFire, FireFighter, CellsOnFire]((fireCells, f) =>
      f.action(fireCells) match
        case Some(Extinguish) =>
          (
            f.copy(loaded = false),
            f.actionableCells.map(d =>
              (d._1 + f.position._1, d._2 + f.position._2)
            ).filter(fireCells.contains).toSet
          )
        case Some(Reload) => (f.copy(loaded = true), Set.empty[(Int, Int)])
        case _            => (f, Set.empty[(Int, Int)])
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
    distance: ((Int, Int), (Int, Int)) => Double
)
