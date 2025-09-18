package it.unibo.firesim.model.firefighters

import it.unibo.firesim.model.map.{Offset, Position}

/** Utility object for operations related to a firefighter's state transitions.
  */
object FireFighterState:
  import it.unibo.firesim.model.monads.ReaderStates.ReaderState
  import it.unibo.firesim.model.firefighters.FireFighterUtils.*
  import it.unibo.firesim.config.Config.correctionThreshold
  type CellsOnFire = Set[Position]

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
            .map(_.minBy(f.score))
            .filter(candidate =>
              !fireCells.contains(
                f.target
              ) || f.score(candidate) < f.score(f.target) * correctionThreshold
            ).getOrElse(f.target)
        )
      (f.when(_.target != newTarget)(_ changeTargetTo newTarget).move, ())
    )

  /** Executes an action to either extinguish a fire or reload, if the
    * conditions to do so are met.
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
        case Some(Reload) => (f.copy(loaded = true), Set.empty[Position])
        case _            => (f, Set.empty[Position])
    )

/** Firefighter unit that extinguishes fire cells, returning to its fire station
  * after every operation to reload.
  *
  * @param station
  *   the coordinates of the fire station.
  * @param neighborsInRay
  *   a set of relative coordinates indicating neighboring positions.
  * @param target
  *   the current target coordinates.
  * @param loaded
  *   indicates whether the firefighter is loaded with water/foam to extinguish
  *   a fire.
  * @param steps
  *   a lazy list of where the head is the current position and the tail
  *   contains the future planned positions.
  * @param moveStrategy
  *   a function defining the movement algorithm.
  */
case class FireFighter(
    station: Position,
    neighborsInRay: Set[Offset],
    target: Position,
    loaded: Boolean,
    steps: LazyList[Position],
    moveStrategy: (Position, Position) => LazyList[Position]
)
