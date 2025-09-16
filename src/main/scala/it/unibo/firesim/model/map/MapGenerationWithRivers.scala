package it.unibo.firesim.model.map

import it.unibo.firesim.config.Config.*
import CellType.*

import scala.collection.parallel.CollectionConverters.*
import scala.util.Random

class MapGenerationWithRivers extends BaseMapGeneration:

  private def createRiver(
      matrix: Matrix,
      start: (Int, Int),
      random: Random
  ): Matrix =
    var m = matrix
    var (r, c) = start

    var angle = random.nextDouble() * 2 * Math.PI
    var width = 0

    while m.inBounds(r, c) do
      for dr <- -width to width; dc <- -width to width do
        val nr = r + dr
        val nc = c + dc
        if m.inBounds(nr, nc) then
          m = m.update(nr, nc, Water)

      angle += (random.nextDouble() - 0.5) * 0.6

      r += Math.round(Math.sin(angle)).toInt
      c += Math.round(Math.cos(angle)).toInt

      if random.nextDouble() < 0.3 then
        val delta = random.nextInt() % 3 - 1
        width = (width + delta).max(0).min(3)

    m

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with water terrain as lakes and rivers
    */
  override def addWater(matrix: Matrix, random: Random): Matrix =
    val rows = matrix.rows
    val cols = matrix.cols
    val lakeSeedsCount = roundedMeanMul(lakeSeedFrequency, rows, cols) max 1
    val lakeSeeds = generateSeeds(rows, cols, lakeSeedsCount, random)
    val minLakeSize = roundedMeanMul(minLakeSizeRatio, rows, cols)
    val maxLakeSize = roundedMeanMul(maxLakeSizeRatio, rows, cols)

    var m = lakeSeeds.par.foldLeft(matrix) { (m, seed) =>
      growCluster(
        m,
        seed,
        random.between(minLakeSize, maxLakeSize),
        lakeGrowthProbability,
        Water,
        random
      )
    }

    lakeSeeds.foreach { lakeSeed =>
      m = createRiver(m, lakeSeed, random)
    }

    m
