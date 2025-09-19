package it.unibo.firesim.model.map

import it.unibo.firesim.model.map.CellType.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class MapBuilderTest extends AnyFlatSpec with Matchers:

  "MapBuilder" should "build a matrix with water" in {
    val mapBuilder = MapBuilder(100, 100, Random(), MapGenerationWithRivers())
    val withWater = mapBuilder.withWater().build
    withWater.positionsOf(Water).length should be > 0
  }

  it should "build a matrix with forests" in {
    val mapBuilder = MapBuilder(100, 100, Random(), MapGenerationWithRivers())
    val withForests = mapBuilder.withForests().build
    withForests.positionsOf(Forest).length should be > 0
  }

  it should "build a matrix with grass, only around some forests" in {
    val mapBuilder = MapBuilder(100, 100, Random(), MapGenerationWithRivers())
    val noGrass = mapBuilder.withGrass().build
    val withGrass = mapBuilder.withForests().withGrass().build

    noGrass.positionsOf(Grass) shouldEqual Seq.empty
    withGrass.positionsOf(Grass).length should be > 0
  }

  it should "build a matrix with stations" in {
    val mapBuilder = MapBuilder(100, 100, Random(), MapGenerationWithRivers())
    val withStations = mapBuilder.withStations().build
    withStations.positionsOf(Station).length should be > 0
  }

  it should "build a matrix with fires, only on forests or grass" in {
    val mapBuilder = MapBuilder(100, 100, Random(), MapGenerationWithRivers())
    val noFires = mapBuilder.withFires().build
    val withFires = mapBuilder.withForests().withFires().build

    noFires.positionsOfBurning() shouldEqual Seq.empty
    withFires.positionsOfBurning().length should be > 0
  }

  it should "build a matrix with custom terrain" in {
    val mapBuilder = MapBuilder(100, 100, Random(), MapGenerationWithRivers())
    val withCustomTerrain =
      mapBuilder.withCustomTerrain(Seq(
        ((0, 0), Forest),
        ((1, 1), Forest)
      )).build
    withCustomTerrain.positionsOf(Forest) shouldEqual Seq((0, 0), (1, 1))
  }
