package it.unibo.firesim.model.firefighters

import it.unibo.firesim.model.firefighters.builder.FireFighterDSL.*
import it.unibo.firesim.model.firefighters.FireFighterState.*
import it.unibo.firesim.model.map.Position
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FireFighterTest extends AnyFlatSpec with Matchers:
  private val station = (0, 0)
  private val ray = 1

  private val updater =
    for
      _ <- moveStep
      extinguishedCells <- actionStep
    yield extinguishedCells

  val fireFighter: FireFighter = createFireFighter:
    withRay(ray)
    stationedIn(station)

  "FireFighter" should "move towards the closest fire from the station" in {
    val cellsOnFire = Set((3, 0), (0, 4))
    val (updated, _) = updater(cellsOnFire, fireFighter)
    val (updated2, _) = updater(cellsOnFire, updated)
    updated.steps.head should be((1, 0))
    updated2.steps.head should be((2, 0))
  }

  it should "use Bresenham algorithm to move" in {
    val cellsOnFire = Set((4, 2))
    val (s1, _) = updater(cellsOnFire, fireFighter)
    val (s2, _) = updater(cellsOnFire, s1)
    val (s3, _) = updater(cellsOnFire, s2)
    val (s4, _) = updater(cellsOnFire, s3)
    s1.steps.head should be((1, 1))
    s2.steps.head should be((2, 1))
    s3.steps.head should be((3, 2))
    s4.steps.head should be((4, 2))
  }

  it should "extinguish cell only when it reaches its targeted cell on fire" in {
    val cellsOnFire = Set((2, 0))
    val (s1, e1) = updater(cellsOnFire, fireFighter)
    val (s2, e2) = updater(cellsOnFire, s1)
    (s1.steps.head, e1) should be(((1, 0), Set()))
    (s2.steps.head, e2) should be(((2, 0), cellsOnFire))
  }

  it should "adjust its direction if cells with a lower enough score are set on fire" in {
    val initialFires = Set((4, 0))
    val updatedFires = initialFires + ((1, 1))
    val (s1, _) = updater(initialFires, fireFighter)
    val (s2, _) = updater(updatedFires, s1)
    s1.steps.head should be((1, 0))
    s2.steps.head should be((1, 1))
  }

  it should "not adjust its direction if cells with a slightly lower score are set on fire" in {
    val initialFires = Set((10, 0), (0, 9))
    val (s1, _) = updater(initialFires, fireFighter)
    val fires1 = initialFires ++ Set((9, 0), (8, 0))
    val (s2, _) = updater(fires1, s1)
    s1.steps.head should be((0, 1))
    s2.steps.head should be((0, 2))
  }

  it should "adjust its direction using Bresenham algorithm" in {
    val initialFires = Set((2, 6))
    val updatedFires = initialFires + ((2, 1))
    val (s1, _) = updater(initialFires, fireFighter)
    val (s2, _) = updater(initialFires, s1)
    val (s3, _) = updater(initialFires, s2)
    val (s4, _) = updater(updatedFires, s3)
    val (s5, _) = updater(updatedFires, s4)
    s1.steps.head should be((0, 1))
    s2.steps.head should be((1, 2))
    s3.steps.head should be((1, 3))
    s4.steps.head should be((2, 2))
    s5.steps.head should be((2, 1))
  }

  it should "extinguish all cells on fire in its range" in {
    val cellsOnFire = Set((0, 1), (0, 2), (1, 2))
    val (s1, e1) = updater(cellsOnFire, fireFighter)
    (s1.steps.head, e1) should be(((0, 1), cellsOnFire))
  }

  it should "return to the station to recharge after every operation" in {
    val closestFire = (2, 0)
    val furthestFire = (0, 3)
    val initialFires = Set(closestFire, furthestFire)
    val updatedFires = Set(furthestFire)
    val (s1, _) = updater(initialFires, fireFighter)
    val (s2, _) = updater(initialFires, s1)
    val (s3, _) = updater(updatedFires, s2)
    val (s4, _) = updater(updatedFires, s3)
    val (s5, _) = updater(updatedFires, s4)
    val (s6, _) = updater(updatedFires, s5)
    val (s7, _) = updater(updatedFires, s6)
    s1.steps.head should be((1, 0))
    s2.steps.head should be((2, 0))
    s3.steps.head should be((1, 0))
    s4.steps.head should be(station)
    s5.steps.head should be((0, 1))
    s6.steps.head should be((0, 2))
    s7.steps.head should be((0, 3))
  }

  it should "not extinguish cells while unloaded" in {
    val initialFires = Set((2, 0))
    val updatedFires = Set((1, 0))
    val (s1, _) = updater(initialFires, fireFighter)
    val (s2, _) = updater(initialFires, s1)
    val (_, e3) = updater(updatedFires, s2)
    e3.isEmpty should be(true)
  }

  it should "return to the station if there are no more cells on fire" in {
    val initialFires = Set((2, 0))
    val (s1, _) = updater(initialFires, fireFighter)
    val (s2, _) = updater(Set.empty[Position], s1)
    s2.steps.head should be((0, 0))
  }
