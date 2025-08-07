package it.unibo.firesim.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps

class FireFighterTest extends AnyFlatSpec with Matchers:
  private val rows = 5
  private val cols = 5
  private val station = (0, 0)

  "FireFighter" should "move towards the closest fire" in {
    val fireFighter = FireFighter(rows, cols, station)
    val cellsOnFire = Seq((3, 0), (0, 4))
    fireFighter.act(cellsOnFire).position should be((1, 0))
    fireFighter.act(cellsOnFire).position should be((2, 0))
  }

  it should "extinguish cell only when it reaches a cell on fire" in {
    val fireFighter = FireFighter(rows, cols, station)
    val cellsOnFire = Seq((2, 0))
    fireFighter.act(cellsOnFire) should be(FireFighterUpdate((1, 0), Seq()))
    fireFighter.act(cellsOnFire) should be(FireFighterUpdate(
      (2, 0),
      cellsOnFire
    ))
  }

  it should "adjust it direction if closer cells are set on fire" in {
    val fireFighter = FireFighter(rows, cols, station)
    val initialCellsOnFire = Seq((4, 0))
    val updatedCellsOnFire = initialCellsOnFire :+ (2, 1)
    fireFighter.act(initialCellsOnFire).position should be((1, 0))
    fireFighter.act(updatedCellsOnFire).position should be((2, 1))
  }

  it should "extinguish all cells on fire in its range" in {
    val fireFighter = FireFighter(rows, cols, station)
    val cellsOnFire = Seq((0, 1), (0, 2), (1, 2))
    fireFighter.act(cellsOnFire) should be(FireFighterUpdate(
      (0, 1),
      cellsOnFire
    ))
  }

  it should "give priority to cells on fire with an higher number of neighbors" in {
    val fireFighter = FireFighter(rows, cols, station)
    val cellsOnFire = Seq((1, 0), (1, 1), (1, 2))
    fireFighter.act(cellsOnFire) should be(FireFighterUpdate(
      (1, 1),
      cellsOnFire
    ))
  }

  it should "return to the station to recharge after every operation" in {
    val fireFighter = FireFighter(rows, cols, station)
    val closestFire = (2, 0)
    val furthestFire = (0, 3)
    val initialCellsOnFire = Seq(closestFire, furthestFire)
    val updatedCellsOnFire = Seq(furthestFire)
    fireFighter.act(initialCellsOnFire).position should be((1, 0))
    fireFighter.act(initialCellsOnFire).position should be((2, 0))
    fireFighter.act(updatedCellsOnFire).position should be((1, 0))
    fireFighter.act(updatedCellsOnFire).position should be(station)
    fireFighter.act(updatedCellsOnFire).position should be((0, 1))
    fireFighter.act(updatedCellsOnFire).position should be((0, 2))
    fireFighter.act(updatedCellsOnFire).position should be((0, 3))
  }

  it should "not extinguish cells while unloaded" in {
    val fireFighter = FireFighter(rows, cols, station)
    val initialCellsOnFire = Seq((2, 0))
    val updatedCellsOnFire = Seq((1, 0))
    fireFighter.act(initialCellsOnFire)
    fireFighter.act(initialCellsOnFire)
    fireFighter.act(updatedCellsOnFire).extinguishedCells.isEmpty should be(
      true
    )
  }
