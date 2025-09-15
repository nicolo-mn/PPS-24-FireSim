package it.unibo.firesim.model.map

import it.unibo.firesim.config.Config.*
import CellType.*
import it.unibo.firesim.model.fire.FireStage

import scala.collection.parallel.CollectionConverters.*
import scala.annotation.tailrec
import scala.util.Random

trait MapGenerationStrategy:
  def addLakes(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addForests(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addGrass(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addStations(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addFires(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addCustomTerrain(
      matrix: Matrix,
      row: Int,
      col: Int,
      cellType: CellType
  ): Matrix

class BaseMapGeneration extends MapGenerationStrategy:

  private def roundedMeanMul(ratio: Double, rows: Int, cols: Int): Int =
    (ratio * (rows + cols) / 2).round.toInt

  private def generateSeeds(
      rows: Int,
      cols: Int,
      count: Int,
      random: Random
  ): Seq[(Int, Int)] =
    Seq.fill(count)((random.nextInt(rows), random.nextInt(cols)))

  private def generateSparseSeeds(
      rows: Int,
      cols: Int,
      count: Int,
      matrix: Matrix,
      random: Random
  ): Seq[(Int, Int)] =

    val emptyCells = (0 until rows).flatMap { r =>
      (0 until cols).collect {
        case c if matrix(r)(c) == Rock => (r, c)
      }
    }
    if emptyCells.isEmpty then
      return Seq((random.nextInt(rows), random.nextInt(cols)))
    random.shuffle(emptyCells).take(count)

  private def growCluster(
      matrix: Matrix,
      seed: (Int, Int),
      clusterSize: Int,
      growthProbability: Double,
      growthType: CellType,
      random: Random
  ): Matrix =

    @tailrec
    def expand(
        queue: Seq[(Int, Int)],
        visited: Set[(Int, Int)],
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

  override def addLakes(
      matrix: Matrix,
      rows: Int,
      cols: Int,
      random: Random
  ): Matrix =
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

  override def addForests(
      matrix: Matrix,
      rows: Int,
      cols: Int,
      random: Random
  ): Matrix =
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

  override def addGrass(
      matrix: Matrix,
      rows: Int,
      cols: Int,
      random: Random
  ): Matrix =
    val grassSeeds: Seq[(Int, Int)] = matrix.positionsOf(Forest).par
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

  override def addStations(
      matrix: Matrix,
      rows: Int,
      cols: Int,
      random: Random
  ): Matrix =
    val stationSeedsCount =
      roundedMeanMul(stationSeedsFrequency, rows, cols) max 1
    val stationSeeds =
      generateSparseSeeds(rows, cols, stationSeedsCount, matrix, random)
    stationSeeds.par.foldLeft(matrix)((m, pos) =>
      m.update(pos._1, pos._2, Station)
    )

  override def addFires(
      matrix: Matrix,
      rows: Int,
      cols: Int,
      random: Random
  ): Matrix =
    var m = matrix
    val forestFireSeedsCount =
      roundedMeanMul(forestFireSeedFrequency, rows, cols) max 1
    val grassFireSeedsCount = roundedMeanMul(grassFireSeedFrequency, rows, cols)

    val forestFires =
      random.shuffle(m.positionsOf(Forest)).take(forestFireSeedsCount)
    val grassFires =
      random.shuffle(m.positionsOf(Grass)).take(grassFireSeedsCount)

    forestFires.foreach((r, c) =>
      m = m.update(r, c, Burning(0, FireStage.Ignition, Forest))
    )
    grassFires.foreach((r, c) =>
      m = m.update(r, c, Burning(0, FireStage.Ignition, Grass))
    )

    m

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
