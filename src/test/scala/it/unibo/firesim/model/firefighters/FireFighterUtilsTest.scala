package it.unibo.firesim.model.firefighters

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.firefighters.FireFighterUtils.*
import it.unibo.firesim.model.firefighters.builder.FireFighterDSL.*

class FireFighterUtilsTest extends AnyFlatSpec with Matchers:
  private val f =
    createFireFighter:
      withRay(1)
      stationedIn(0, 0)

  "move" should "advance the firefighter to the next step" in {
    val moved = f.changeTargetTo((2, 0)).move
    moved.position should be((1, 0))
  }

  "position" should "return the current step" in {
    val f1 = f.changeTargetTo((2, 0)).move
    f.position should be((0, 0))
    f1.position should be((1, 0))
  }

  "changeTargetTo" should "update the target and recompute steps" in {
    val updated = f.changeTargetTo((0, 2))
    updated.target should be((0, 2))
    updated.steps.head should be((0, 0))
    updated.steps.tail.head should be((0, 1))
  }

  "when" should "apply the transformation if condition is true" in {
    val updated = f.when(_ => true)(_.changeTargetTo((1, 1)))
    updated.target should be((1, 1))
  }

  it should "leave the firefighter unchanged if condition is false" in {
    val updated = f.when(_ => false)(_.changeTargetTo((1, 1)))
    updated.target should be((0, 0))
  }

  "action" should "return Extinguish when loaded and at fire target" in {
    val f1 = f.changeTargetTo((1, 0)).move
    f1.action(Set((1, 0))) should be(Some(FireFighterAction.Extinguish))
  }

  it should "return Reload when unloaded and at the station" in {
    f.copy(loaded = false).action(Set.empty) should be(Some(FireFighterAction.Reload))
  }

  it should "return None if no action is possible" in {
    f.action(Set((2, 0))) should be(None) // not at target yet
  }

  "score" should "compute a lower score for fires closer to station and the player" in {
    val nearStation = (1, 0)
    val farFromStation = (5, 5)
    f.score(nearStation) should be < f.score(farFromStation)
  }
