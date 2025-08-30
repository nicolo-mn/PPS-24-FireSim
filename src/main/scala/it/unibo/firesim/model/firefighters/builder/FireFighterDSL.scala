package it.unibo.firesim.model.firefighters.builder

import it.unibo.firesim.model.firefighters.FireFighter

object FireFighterDSL:

  def createFireFighter(instruction: FireFighterBuilder ?=> Unit): FireFighter =
    given builder: FireFighterBuilder = FireFighterBuilder()
    instruction(using builder)
    builder.build()

  def withRay(ray: Int)(using builder: FireFighterBuilder): Unit =
    builder.withRay(ray)

  def stationedIn(s: (Int, Int))(using builder: FireFighterBuilder): Unit =
    builder.stationedIn(s)
