package it.unibo.firesim.model.map

import it.unibo.firesim.model.map.CellType.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class MapGenerationWithRiversTest extends AnyFlatSpec with Matchers:

  private val d = 100
  private val allRocks = Vector.tabulate(d, d)((row, col) => Rock)

  "MapGenerationWithRivers" should "be able to add lakes and rivers to the matrix, and rivers should always touch the borders of the map" in {
    val withWater = MapGenerationWithRivers().addWater(allRocks, new Random())
    val water = withWater.positionsOf(Water)
    water.length should be > 0
    water.count((r, c) =>
      r == 0 || r == d - 1 || c == 0 || c == d - 1
    ) should be > 0
  }
