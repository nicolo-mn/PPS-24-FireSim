package it.unibo.firesim.model.firefighters

object FireFighterState:
  import it.unibo.firesim.model.monads.ReaderStates.ReaderState
  import it.unibo.firesim.model.firefighters.FireFighterUtils.*
  private type CellsOnFire = Set[(Int, Int)]

  def moveStep: ReaderState[CellsOnFire, FireFighter, Unit] =
    ReaderState[CellsOnFire, FireFighter, Unit]((fireCells, f) =>
      val newTarget = Option.when(f.loaded)(fireCells)
        .collect { case c if c.nonEmpty => c.minBy(f.distance(f.position, _)) }
        .filter(candidate =>
          !fireCells.contains(f.target) ||
            f.isCloseToStation(candidate) ||
            f.isCloseToTarget(candidate)
        )
        .getOrElse(if f.loaded then f.target else f.station)
      (f.when(_.target != newTarget)(_ changeTargetTo newTarget).move, ())
    )

  def extinguishStep
      : ReaderState[CellsOnFire, FireFighter, CellsOnFire] =
    ReaderState[CellsOnFire, FireFighter, CellsOnFire]((fireCells, f) =>
      import it.unibo.firesim.model.firefighters.FireFighterUtils.FireFighterAction.*
      f.action(fireCells) match
        case Some(Extinguish) =>
          (
            f.copy(loaded = false),
            f.neighborsInRay.map(d =>
              (d._1 + f.position._1, d._2 + f.position._2)
            ).intersect(fireCells)
          )
        case Some(Reload) => (f.copy(loaded = true), Set.empty[(Int, Int)])
        case _            => (f, Set.empty[(Int, Int)])
    )

/** Firefighter unit that extinguishes fire cells, returning to its fire station
  * after every operation to reload
  */
case class FireFighter(
    station: (Int, Int),
    neighborsInRay: Set[(Int, Int)],
    position: (Int, Int),
    target: (Int, Int),
    loaded: Boolean,
    moveStrategy: MoveStrategy,
    distance: ((Int, Int), (Int, Int)) => Double
)
