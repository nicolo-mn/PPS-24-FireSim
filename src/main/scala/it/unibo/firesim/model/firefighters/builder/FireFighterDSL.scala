package it.unibo.firesim.model.firefighters.builder

import it.unibo.firesim.model.firefighters.FireFighter

/** DSL for creating and configuring a firefighter, provides helper methods to
  * build a firefighter instance.
  */
object FireFighterDSL:

  /** Creates a configured FireFighter.
    *
    * @param instructions
    *   a function to configure a given FireFighterBuilder
    * @return
    *   a built FireFighter instance
    */
  def createFireFighter(instructions: FireFighterBuilder ?=> Unit)
      : FireFighter =
    given builder: FireFighterBuilder = FireFighterBuilder()
    instructions(using builder)
    builder.build()

  /** Configures the action ray for the firefighter.
    *
    * @param ray
    *   the range of action for the firefighter
    */
  def withRay(ray: Int)(using builder: FireFighterBuilder): Unit =
    builder.withRay(ray)

  /** Configures the base station for the firefighter.
    *
    * @param s
    *   a tuple of coordinates representing the station location
    */
  def stationedIn(s: (Int, Int))(using builder: FireFighterBuilder): Unit =
    builder.stationedIn(s)
