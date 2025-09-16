package it.unibo.firesim.model.firefighters

/** Utility object for operations related to a firefighter's state transitions.
  */
object FireFighterState:
  import it.unibo.firesim.model.monads.ReaderStates.ReaderState
  import it.unibo.firesim.model.firefighters.FireFighterUtils.*
  import it.unibo.firesim.util.ChebyshevDistance.distance
  private type CellsOnFire = Set[(Int, Int)]

  /** Represent a move step for a firefighter. Determines the new target based
    * on the current state and fire cells, updating the firefighter.
    *
    * @return
    *   a ReaderState that transforms a FireFighter by moving it.
    */
  def moveStep: ReaderState[CellsOnFire, FireFighter, Unit] =
    ReaderState[CellsOnFire, FireFighter, Unit]((fireCells, f) =>
      val newTarget = Option.when(!f.loaded || fireCells.isEmpty)(f.station)
        .getOrElse(
          Option(fireCells)
            .map(_.minBy(distance(f.position, _)))
            .filter(candidate =>
              !fireCells.contains(f.target) ||
                f.isCloseToStation(candidate) ||
                f.isCloseToTarget(candidate)
            ).getOrElse(f.target)
        )
      (f.when(_.target != newTarget)(_ changeTargetTo newTarget).move, ())
    )

  /** Executes an action to either extinguish a fire or reload equipment.
    *
    * @return
    *   a ReaderState that updates the FireFighter and returns the affected
    *   cells.
    */
  def actionStep
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
  * after every operation to reload.
  *
  * @param station
  *   the coordinates of the fire station.
  * @param neighborsInRay
  *   a set of relative coordinates indicating neighboring positions.
  * @param position
  *   the current position of the firefighter.
  * @param target
  *   the current target coordinates.
  * @param loaded
  *   indicates whether the firefighter is equipped.
  * @param nextSteps
  *   a lazy list of planned future positions.
  * @param moveStrategy
  *   a function defining the movement strategy.
  */
case class FireFighter(
    station: (Int, Int),
    neighborsInRay: Set[(Int, Int)],
    position: (Int, Int),
    target: (Int, Int),
    loaded: Boolean,
    nextSteps: LazyList[(Int, Int)],
    moveStrategy: ((Int, Int), (Int, Int)) => LazyList[(Int, Int)]
)
