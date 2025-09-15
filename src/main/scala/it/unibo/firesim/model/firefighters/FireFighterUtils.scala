package it.unibo.firesim.model.firefighters

import it.unibo.firesim.config.Config.{stationThreshold, targetThreshold}

/** Utility object providing helper methods and actions for a firefighter.
  */
object FireFighterUtils:

  /** Enumeration representing the possible actions a firefighter can take.
    */
  enum FireFighterAction:
    case Extinguish
    case Reload

  /** Extension methods for the FireFighter class.
    *
    */
  extension (f: FireFighter)

    /** Updates the firefighter by moving to the next position in the planned
      * steps.
      *
      * @return
      *   an updated FireFighter with the new position and steps.
      */
    def move: FireFighter =
      f.copy(nextSteps = f.nextSteps.tail, position = f.nextSteps.head)

    /** Changes the target of the firefighter and computes new movement steps.
      *
      * @param target
      *   the new target position.
      * @return
      *   an updated FireFighter with a new target and movement steps.
      */
    def changeTargetTo(target: (Int, Int)): FireFighter =
      f.copy(
        nextSteps = f.moveStrategy(f.position, target),
        target = target
      )

    /** Updates the firefighter if a condition is met.
      *
      * @param cond
      *   a predicate.
      * @param map
      *   a function to transform the firefighter if the condition is met.
      * @return
      *   an updated FireFighter if the condition holds; otherwise, the
      *   original.
      */
    def when(cond: FireFighter => Boolean)(map: FireFighter => FireFighter)
        : FireFighter =
      if cond(f) then map(f) else f

    /** Determines the appropriate action based on the current position and fire
      * cells.
      *
      * If the firefighter is loaded and at a fire cell matching its target,
      * returns Extinguish. Otherwise, if not loaded and at the station, returns
      * Reload.
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

    /** Checks if a position is sufficiently close to the base station.
      *
      * @param candidate
      *   the candidate position.
      * @return
      *   true if the candidate is within the allowable distance from the
      *   station.
      */
    def isCloseToStation(candidate: (Int, Int)): Boolean = f.distance(
      candidate,
      f.station
    ) < f.distance(f.target, f.station) * stationThreshold

    /** Checks if a position is sufficiently close to the current target.
      *
      * @param candidate
      *   the candidate position.
      * @return
      *   true if the candidate is within the allowable distance from the
      *   target.
      */
    def isCloseToTarget(candidate: (Int, Int)): Boolean = f.distance(
      f.target,
      candidate
    ) < f.distance(f.target, f.station) * targetThreshold
