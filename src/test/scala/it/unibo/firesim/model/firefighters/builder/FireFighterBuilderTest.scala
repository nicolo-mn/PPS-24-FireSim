package it.unibo.firesim.model.firefighters.builder

import it.unibo.firesim.model.firefighters.FireFighter
import it.unibo.firesim.model.firefighters.FireFighterUtils.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FireFighterBuilderTest extends AnyFlatSpec with Matchers:

  "FireFighterBuilder" should "create a firefighter with correct station and ray" in {
    val builder = new FireFighterBuilder
    builder.withRay(1)
    builder.stationedIn((2, 3))
    val f: FireFighter = builder.build()

    f.station should be((2, 3))
    f.position should be((2, 3))
    f.neighborsInRay should contain allElementsOf Set(
      (-1, -1),
      (-1, 0),
      (-1, 1),
      (0, -1),
      (0, 0),
      (0, 1),
      (1, -1),
      (1, 0),
      (1, 1)
    )
  }

  it should "throw an exception if ray is negative" in {
    val builder = new FireFighterBuilder
    an[IllegalArgumentException] should be thrownBy builder.withRay(-1)
  }

  it should "throw an exception if stationed in negative coordinates" in {
    val builder = new FireFighterBuilder
    an[IllegalArgumentException] should be thrownBy builder.stationedIn((-1, 0))
    an[IllegalArgumentException] should be thrownBy builder.stationedIn((0, -5))
  }

  it should "throw an exception if build is called before setting ray or station" in {
    val builder1 = new FireFighterBuilder
    builder1.withRay(1)
    val builder2 = new FireFighterBuilder
    builder2.stationedIn((0, 0))

    an[IllegalArgumentException] should be thrownBy builder1.build()
    an[IllegalArgumentException] should be thrownBy builder2.build()
  }

  it should "generate neighbors in ray correctly" in {
    val builder = new FireFighterBuilder
    builder.withRay(1)
    builder.stationedIn((0, 0))
    val f = builder.build()

    val expectedNeighbors = Set(
      (-1, -1),
      (-1, 0),
      (-1, 1),
      (0, -1),
      (0, 0),
      (0, 1),
      (1, -1),
      (1, 0),
      (1, 1)
    )
    f.neighborsInRay shouldBe expectedNeighbors
  }

  it should "create a LazyList that continually returns the position of the station" in {
    val builder = new FireFighterBuilder
    builder.withRay(0)
    builder.stationedIn((5, 5))
    val f = builder.build()

    f.steps.take(3).toList shouldBe List((5, 5), (5, 5), (5, 5))
  }
