package it.unibo.firesim.model.map

import it.unibo.firesim.model.map.CellType.*
import it.unibo.firesim.model.map.MapBuilderDSL.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class MapBuilderDSLTest extends AnyFlatSpec with Matchers:

  "MapBuilderDSL" should "ease the build of a matrix with water" in {
    val wWater = buildMap(100, 100, MapGenerationWithRivers()):
      withWater
    wWater.positionsOf(Water).length should be > 0
  }

  it should "ease the build of a matrix with forests" in {
    val wForests = buildMap(100, 100, MapGenerationWithRivers()):
      withForests
    wForests.positionsOf(Forest).length should be > 0
  }

  it should "ease the build of a matrix with grass, only around some forests" in {
    val noGrass = buildMap(100, 100, MapGenerationWithRivers()):
      withGrass
    val wGrass = buildMap(100, 100, MapGenerationWithRivers()):
      withForests
      withGrass

    noGrass.positionsOf(Grass) shouldEqual Seq.empty
    wGrass.positionsOf(Grass).length should be > 0
  }

  it should "ease the build of a matrix with stations" in {
    val wStations = buildMap(100, 100, MapGenerationWithRivers()):
      withStations
    wStations.positionsOf(Station).length should be > 0
  }

  it should "ease the build of a matrix with fires, only on forests or grass" in {
    val noFires = buildMap(100, 100, MapGenerationWithRivers()):
      withFires
    val wFires = buildMap(100, 100, MapGenerationWithRivers()):
      withForests
      withFires

    noFires.positionsOfBurning() shouldEqual Seq.empty
    wFires.positionsOfBurning().length should be > 0
  }

  it should "ease the build of a matrix with custom terrain" in {
    val wCustomTerrain =
      buildMap(100, 100, MapGenerationWithRivers()):
        withCustomTerrain(Seq(
          ((0, 0), Forest),
          ((1, 1), Forest)
        ))
    wCustomTerrain.positionsOf(Forest) shouldEqual Seq((0, 0), (1, 1))
  }
