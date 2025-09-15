package it.unibo.firesim.model.firefighters.builder

import it.unibo.firesim.model.firefighters.builder.FireFighterDSL.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps

class FireFighterDSLTest extends AnyFlatSpec with Matchers:
  "FireFighterDSL" should "create firefighters with significant indentation" in {
    noException should be thrownBy:
      createFireFighter:
        withRay(1)
        stationedIn(0, 0)
  }

  it should "create firefighters with given station" in {
    val firefighter1 = createFireFighter:
      withRay(1)
      stationedIn(0, 0)

    val firefighter2 = createFireFighter:
      withRay(1)
      stationedIn(1, 1)

    firefighter1.position should be(0, 0)
    firefighter2.position should be(1, 1)
  }

  it should "create firefighters with given ray" in {
    val firefighter1 = createFireFighter:
      withRay(0)
      stationedIn(0, 0)

    val firefighter2 = createFireFighter:
      withRay(1)
      stationedIn(1, 1)

    firefighter1.neighborsInRay should be(Set((0, 0)))

    firefighter2.neighborsInRay should be(Set(
      (-1, -1),
      (-1, 0),
      (-1, 1),
      (0, -1),
      (0, 0),
      (0, 1),
      (1, -1),
      (1, 0),
      (1, 1)
    ))
  }
