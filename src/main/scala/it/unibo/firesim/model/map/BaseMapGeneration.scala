package it.unibo.firesim.model.map

import it.unibo.firesim.config.Config.*
import it.unibo.firesim.model.fire.FireStage
import it.unibo.firesim.model.map.CellType.*

import scala.collection.parallel.CollectionConverters.*
import scala.annotation.tailrec
import scala.util.Random

class BaseMapGeneration extends MapGenerationStrategy:

  protected def roundedMeanMul(ratio: Double, rows: Int, cols: Int): Int =
    (ratio * (rows + cols) / 2).round.toInt

  protected def generateSeeds(
      rows: Int,
      cols: Int,
      count: Int,
      random: Random
  ): Seq[Position] =
    Seq.fill(count)((random.nextInt(rows), random.nextInt(cols)))

  private def generateSparseSeeds(
      rows: Int,
      cols: Int,
      count: Int,
      matrix: Matrix,
      random: Random
  ): Seq[Position] =

    val emptyCells = (0 until rows).flatMap { r =>
      (0 until cols).collect {
        case c if matrix(r)(c) == Rock => (r, c)
      }
    }
    if emptyCells.isEmpty then
      return Seq((random.nextInt(rows), random.nextInt(cols)))
    random.shuffle(emptyCells).take(count)

  protected def growCluster(
      matrix: Matrix,
      seed: Position,
      clusterSize: Int,
      growthProbability: Double,
      growthType: CellType,
      random: Random
  ): Matrix =

    @tailrec
    def expand(
        queue: Seq[Position],
        visited: Set[Position],
        count: Int,
        m: Matrix
    ): Matrix =
      if count >= clusterSize || queue.isEmpty then m
      else
        val (r, c) = queue.head
        val newMatrix = m.update(r, c, growthType)
        val next = newMatrix.neighbors(r, c)
          .filterNot(visited.contains)
          .filter((r, c) => m(r)(c) == Rock)
          .filter(_ => random.nextDouble() < growthProbability)
        expand(queue.tail ++ next, visited + ((r, c)), count + 1, newMatrix)

    expand(Seq(seed), Set.empty, 0, matrix)

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with water terrain as lakes
    */
  override def addWater(matrix: Matrix, random: Random): Matrix =
    val rows = matrix.rows
    val cols = matrix.cols
    val lakeSeedsCount = roundedMeanMul(lakeSeedFrequency, rows, cols) max 1
    val lakeSeeds = generateSeeds(rows, cols, lakeSeedsCount, random)
    val minLakeSize = roundedMeanMul(minLakeSizeRatio, rows, cols)
    val maxLakeSize = roundedMeanMul(maxLakeSizeRatio, rows, cols)
    lakeSeeds.par.foldLeft(matrix) { (m, seed) =>
      growCluster(
        m,
        seed,
        random.between(minLakeSize, maxLakeSize),
        lakeGrowthProbability,
        Water,
        random
      )
    }

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with forest terrain as clusters
    */
  override def addForests(matrix: Matrix, random: Random): Matrix =
    val rows = matrix.rows
    val cols = matrix.cols
    val forestSeedsCount = roundedMeanMul(forestSeedFrequency, rows, cols) max 1
    val forestSeeds = generateSeeds(rows, cols, forestSeedsCount, random)

    val minForestSize = roundedMeanMul(minForestSizeRatio, rows, cols)
    val maxForestSize = roundedMeanMul(maxForestSizeRatio, rows, cols)
    forestSeeds.par.foldLeft(matrix) { (m, seed) =>
      growCluster(
        m,
        seed,
        random.between(minForestSize, maxForestSize),
        forestGrowthProbability,
        Forest,
        random
      )
    }

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with grass terrain as contour to the forest clusters
    */
  override def addGrass(matrix: Matrix, random: Random): Matrix =
    val rows = matrix.rows
    val cols = matrix.cols
    val grassSeeds: Seq[Position] = matrix.positionsOf(Forest).par
      .flatMap((r, c) => matrix.neighbors(r, c))
      .filter((r, c) => matrix(r)(c) == Rock)
      .seq

    val minGrassSpreadDistance = roundedMeanMul(minGrassSizeRatio, rows, cols)
    val maxGrassSpreadDistance = roundedMeanMul(maxGrassSizeRatio, rows, cols)
    grassSeeds.par.foldLeft(matrix) { (matrix, seed) =>
      growCluster(
        matrix,
        seed,
        random.between(minGrassSpreadDistance, maxGrassSpreadDistance),
        grassGrowthProbability,
        Grass,
        random
      )
    }

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with randomly positioned stations, preferably on
    *   rocks
    */
  override def addStations(matrix: Matrix, random: Random): Matrix =
    val rows = matrix.rows
    val cols = matrix.cols
    val stationSeedsCount =
      roundedMeanMul(stationSeedsFrequency, rows, cols) max 1
    val stationSeeds =
      generateSparseSeeds(rows, cols, stationSeedsCount, matrix, random)
    stationSeeds.par.foldLeft(matrix)((m, pos) =>
      m.update(pos._1, pos._2, Station)
    )

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with randomly positioned fires, only on forest or
    *   grass terrain
    */
  override def addFires(matrix: Matrix, random: Random): Matrix =
    val rows = matrix.rows
    val cols = matrix.cols
    var m = matrix
    val forestFireSeedsCount =
      roundedMeanMul(forestFireSeedFrequency, rows, cols) max 1
    val grassFireSeedsCount = roundedMeanMul(grassFireSeedFrequency, rows, cols)

    def generateFires(toBurn: CellType, count: Int): Unit =
      random.shuffle(m.positionsOf(toBurn)).take(count)
        .foreach((r, c) =>
          m = m.update(r, c, Burning(0, FireStage.Ignition, toBurn))
        )

    generateFires(Forest, forestFireSeedsCount)
    generateFires(Grass, grassFireSeedsCount)

    m

  /** @param matrix
    *   The Matrix to modify
    * @param row
    *   the row of the cell
    * @param col
    *   the column of the cell
    * @param cellType
    *   the type of cell
    * @return
    *   the modified Matrix with the custom terrain in the specified position
    */
  override def addCustomTerrain(
      matrix: Matrix,
      row: Int,
      col: Int,
      cellType: CellType
  ): Matrix =
    if matrix.inBounds(row, col) then
      matrix.update(row, col, cellType)
    else
      matrix
