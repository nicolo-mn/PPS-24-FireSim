package it.unibo.firesim.model.fire

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.fire.FireStage.{Active, Ignition, Smoldering}

class FireModelUtilsTest extends AnyFlatSpec with Matchers:
  it should "correctly transition between fire stages based on burn duration" in {
    val burnDuration = 10
    val startCycle = 0

    FireStage.nextStage(startCycle, 1, burnDuration) shouldBe Ignition // 10%
    FireStage.nextStage(startCycle, 4, burnDuration) shouldBe Active // 40%
    FireStage.nextStage(startCycle, 8, burnDuration) shouldBe Active // 80%
    FireStage.nextStage(startCycle, 9, burnDuration) shouldBe Smoldering // 90%
  }

  it should "correctly convert an angle to a WindDirection" in {
    val angleToDirection = Map(
      0.0 -> WindDirection.North,
      45.0 -> WindDirection.NorthEast,
      90.0 -> WindDirection.East,
      135.0 -> WindDirection.SouthEast,
      180.0 -> WindDirection.South,
      225.0 -> WindDirection.SouthWest,
      270.0 -> WindDirection.West,
      315.0 -> WindDirection.NorthWest,
      360.0 -> WindDirection.North
    )

    angleToDirection.foreach { (angle, direction) =>
      fromAngle(angle) shouldBe direction
    }
  }
