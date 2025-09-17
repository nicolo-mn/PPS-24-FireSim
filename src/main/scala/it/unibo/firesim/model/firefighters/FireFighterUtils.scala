package it.unibo.firesim.model.firefighters

import it.unibo.firesim.config.Config.{distanceFromPosWeight, distanceFromStationWeight}
import it.unibo.firesim.util.ChebyshevDistance.distance

/** Utility object providing helper methods and actions for a firefighter.
  */
object FireFighterUtils:

  /** Enumeration representing the possible actions a firefighter can take. */
  enum FireFighterAction:
    case Extinguish
    case Reload

  /** Extension methods for the FireFighter class. */
  extension (f: FireFighter)

    /** Updates the firefighter by moving to the next position in the planned
      * steps.
      *
      * @return
      *   an updated FireFighter with the new position and steps.
      */
    def move: FireFighter =
      f.copy(steps = f.steps.tail)

    /** Firefighter current position
      *
      * @return
      *   the firefighter position
      */
    def position: (Int, Int) = f.steps.head

    /** Changes the target of the firefighter and computes new movement steps.
      *
      * @param target
      *   the new target position.
      * @return
      *   an updated FireFighter with a new target and movement steps.
      */
    def changeTargetTo(target: (Int, Int)): FireFighter =
      f.copy(
        steps = f.moveStrategy(f.position, target),
        target = target
      )

    /** Updates the firefighter if a condition is met.
      *
      * @param cond
      *   a predicate.
      * @param map
      *   a function to transform the firefighter if the condition is met.
      * @return
      *   an updated FireFighter if the condition is met, otherwise, the
      *   original.
      */
    def when(cond: FireFighter => Boolean)(map: FireFighter => FireFighter)
        : FireFighter =
      if cond(f) then map(f) else f

    /** Determines the appropriate action based on the current position and fire
      * cells.
      *
      * If the firefighter is loaded and at a fire cell matching its target,
      * returns Extinguish. If not loaded and at the station, returns Reload.
      * Otherwise, returns an empty Option.
      *
      * @param fireCells
      *   the set of fire cell positions.
      * @return
      *   an Option containing the corresponding FireFighterAction; otherwise,
      *   None.
      */
    def action(fireCells: Set[(Int, Int)]): Option[FireFighterAction] =
      import FireFighterAction.*
      if f.loaded && fireCells.contains(f.position) && f.position == f.target
      then Option(Extinguish)
      else if !f.loaded && f.position == f.station then Option(Reload)
      else None

    /** Determines the weighted score of a candidate target
      *
      * @param candidate
      *   the candidate target to score.
      * @return
      *   the weighted score of a candidate target
      */
    def score(candidate: (Int, Int)): Double =
      distance(candidate, f.station) * distanceFromStationWeight + distance(
        candidate,
        f.position
      ) * distanceFromPosWeight
