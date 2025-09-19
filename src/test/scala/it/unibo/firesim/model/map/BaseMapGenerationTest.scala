package it.unibo.firesim.model.map

import it.unibo.firesim.model.map.CellType.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class BaseMapGenerationTest extends AnyFlatSpec with Matchers:

  private val allForests =
    Vector.tabulate(100, 100)((row, col) => Forest)

  private val allRocks = Vector.tabulate(100, 100)((row, col) => Rock)
  private val someForests = allRocks.update(10, 10, Forest)

  "BaseMapGeneration" should "be able to add lakes to the matrix" in {
    val withWater = BaseMapGeneration().addWater(allRocks, new Random())
    withWater.positionsOf(Water).length should be > 0
  }

  it should "be able to add forests to the matrix" in {
    val withForests = BaseMapGeneration().addForests(allRocks, new Random())
    withForests.positionsOf(Forest).length should be > 0
  }

  it should "be able to add grass to the matrix, only around some forests and on rocks" in {
    val noRockNoGrass = BaseMapGeneration().addGrass(allForests, new Random())
    val noForestsNoGrass = BaseMapGeneration().addGrass(allRocks, new Random())
    val someForestsSomeGrass =
      BaseMapGeneration().addGrass(someForests, new Random())

    noRockNoGrass.positionsOf(Grass) shouldEqual Seq.empty
    noForestsNoGrass.positionsOf(Grass) shouldEqual Seq.empty
    someForestsSomeGrass.positionsOf(Grass).length should be > 0
  }

  it should "be able to add stations to the matrix, possibly on rocks" in {
    val withStations = BaseMapGeneration().addStations(allForests, new Random())
    withStations.positionsOf(Station).length should be > 0
  }

  it should "be able to add fires to the matrix, only on forests or grass" in {
    val withFires = BaseMapGeneration().addFires(allForests, new Random())
    val noFires = BaseMapGeneration().addFires(allRocks, new Random())
    withFires.positionsOfBurning().length should be > 0
    noFires.positionsOfBurning() shouldEqual Seq.empty
  }

  it should "be able to add custom terrain, only if in bounds" in {
    val noCustomTerrain =
      BaseMapGeneration().addCustomTerrain(allForests, -1, -1, Rock)
    val withCustomTerrain =
      BaseMapGeneration().addCustomTerrain(allForests, 0, 0, Rock)

    noCustomTerrain.positionsOf(Rock) shouldEqual Seq.empty
    noCustomTerrain shouldEqual allForests
    withCustomTerrain.positionsOf(Rock) shouldEqual Seq((0, 0))
  }
