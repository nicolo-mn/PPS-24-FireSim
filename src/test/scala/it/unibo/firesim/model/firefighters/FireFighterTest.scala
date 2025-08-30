package it.unibo.firesim.model.firefighters

import it.unibo.firesim.model.firefighters.builder.FireFighterDSL.*
import it.unibo.firesim.model.firefighters.FireFighterState.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FireFighterTest extends AnyFlatSpec with Matchers:
  private val station = (0, 0)
  private val ray = 1

  private val monad =
    for
      _ <- moveStep
      extinguished <- extinguishStep
    yield extinguished

  val fireFighter: FireFighter = createFireFighter:
    withRay(ray)
    stationedIn(station)

  "FireFighter" should "move towards the closest fire" in {
    val cellsOnFire = Set((3, 0), (0, 4))
    val (updated, _) = monad(cellsOnFire, fireFighter)
    val (updated2, _) = monad(cellsOnFire, updated)
    updated.position should be((1, 0))
    updated2.position should be((2, 0))
  }

  it should "use Bresenham algorithm to move" in {
    val cellsOnFire = Set((4, 2))
    val (s1, _) = monad(cellsOnFire, fireFighter)
    val (s2, _) = monad(cellsOnFire, s1)
    val (s3, _) = monad(cellsOnFire, s2)
    val (s4, _) = monad(cellsOnFire, s3)
    s1.position should be((1, 1))
    s2.position should be((2, 1))
    s3.position should be((3, 2))
    s4.position should be((4, 2))
  }

  it should "extinguish cell only when it reaches a cell on fire" in {
    val cellsOnFire = Set((2, 0))
    val (s1, e1) = monad(cellsOnFire, fireFighter)
    val (s2, e2) = monad(cellsOnFire, s1)
    (s1.position, e1) should be(((1, 0), Set()))
    (s2.position, e2) should be(((2, 0), cellsOnFire))
  }

  it should "adjust its direction if closer cells to the station are set on fire" in {
    val initialFires = Set((4, 0))
    val updatedFires = initialFires + ((2, 1))
    val (s1, _) = monad(initialFires, fireFighter)
    val (s2, _) = monad(updatedFires, s1)
    s1.position should be((1, 0))
    s2.position should be((2, 1))
  }

  it should "adjust its direction using Bresenham algorithm" in {
    val initialFires = Set((2, 6))
    val updatedFires = initialFires + ((2, 1))
    val (s1, _) = monad(initialFires, fireFighter)
    val (s2, _) = monad(initialFires, s1)
    val (s3, _) = monad(initialFires, s2)
    val (s4, _) = monad(updatedFires, s3)
    val (s5, _) = monad(updatedFires, s4)
    s1.position should be((0, 1))
    s2.position should be((1, 2))
    s3.position should be((1, 3))
    s4.position should be((2, 2))
    s5.position should be((2, 1))
  }

  it should "extinguish all cells on fire in its range" in {
    val cellsOnFire = Set((0, 1), (0, 2), (1, 2))
    val (s1, e1) = monad(cellsOnFire, fireFighter)
    (s1.position, e1) should be(((0, 1), cellsOnFire))
  }

  it should "return to the station to recharge after every operation" in {
    val closestFire = (2, 0)
    val furthestFire = (0, 3)
    val initialFires = Set(closestFire, furthestFire)
    val updatedFires = Set(furthestFire)
    val (s1, _) = monad(initialFires, fireFighter)
    val (s2, _) = monad(initialFires, s1)
    val (s3, _) = monad(updatedFires, s2)
    val (s4, _) = monad(updatedFires, s3)
    val (s5, _) = monad(updatedFires, s4)
    val (s6, _) = monad(updatedFires, s5)
    val (s7, _) = monad(updatedFires, s6)
    s1.position should be((1, 0))
    s2.position should be((2, 0))
    s3.position should be((1, 0))
    s4.position should be(station)
    s5.position should be((0, 1))
    s6.position should be((0, 2))
    s7.position should be((0, 3))
  }

  it should "not extinguish cells while unloaded" in {
    val initialFires = Set((2, 0))
    val updatedFires = Set((1, 0))
    val (s1, _) = monad(initialFires, fireFighter)
    val (s2, _) = monad(initialFires, s1)
    val (_, e3) = monad(updatedFires, s2)
    e3.isEmpty should be(true)
  }
