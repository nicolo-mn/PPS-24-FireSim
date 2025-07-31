package it.unibo.firesim.model

import it.unibo.firesim.model.cell.CellState.{Burnt, Intact}
import it.unibo.firesim.model.cell.CellType.Forest
import it.unibo.firesim.model.cell.{Cell, CellState, CellType}

import scala.annotation.tailrec
import scala.util.Random

class SimModel(random: Random = Random()) {

  /** Generates a map with the specified number of rows and columns.
   *
   * @param rows Number of rows in the map (height)
   * @param cols Number of columns in the map (width)
   * @return A Matrix containing the generated cells
   */
  def generateMap(rows: Int, cols: Int): Matrix = {
    val matrix = Matrix(Vector.tabulate(rows, cols) { (r, c) => Cell(r, c, CellType.Empty, CellState.Intact) })

    val forestSeedFrequency = 0.01 // 1%
    val forestSeedsCount = ((rows * cols) * forestSeedFrequency).toInt
    val forestSeeds = generateSeeds(rows, cols, forestSeedsCount)

    val minForestSize = 30
    val maxForestSize = 100
    val forestGrowthProbability = 0.7 // 70%
    val withForests = forestSeeds.foldLeft(matrix) { (matrix, seed) =>
      growCluster(matrix, seed, random.between(minForestSize, maxForestSize), forestGrowthProbability, CellType.Forest)
    }

    val grassSeeds: Seq[(Int, Int)] =
      (0 until withForests.rows).flatMap { r =>
        (0 until withForests.cols).flatMap { c =>
          if (withForests(r, c).cellType == CellType.Forest) {
            neighbors(r, c, withForests).filter { case (nr, nc) => withForests(nr, nc).cellType == CellType.Empty }
          } else {
            Seq.empty
          }
        }
      }

    val minGrassSpreadDistance = 10
    val maxGrassSpreadDistance = 40
    val grassGrowthFrequency = 0.8 // 80%
    val withGrass = grassSeeds.foldLeft(withForests) { (matrix, seed) =>
      growCluster(matrix, seed, random.between(minGrassSpreadDistance, maxGrassSpreadDistance), grassGrowthFrequency, CellType.Grass)
    }

    val stationSeedsFrequency = 0.0002 // 0.02%
    val stationSeedsCount = ((rows * cols) * stationSeedsFrequency).toInt
    val stationSeeds = generateSparseSeeds(rows, cols, stationSeedsCount, withGrass)
    val withStations = stationSeeds.foldLeft(withGrass)((m, pos) => m.update(pos._1, pos._2, Cell(pos._1, pos._2, CellType.Station, CellState.Intact)))

    withStations
  }

  private def generateSeeds(rows: Int, cols: Int, count: Int): Seq[(Int, Int)] =
    Seq.fill(count)((random.nextInt(rows), random.nextInt(cols)))

  private def generateSparseSeeds(rows: Int, cols: Int, count: Int, matrix: Matrix): Seq[(Int, Int)] = {
    LazyList.continually((random.nextInt(rows), random.nextInt(cols)))
      .filter((r, c) => matrix(r, c).cellType == CellType.Empty)
      .distinct
      .take(count)
      .toList
  }

  def neighbors(r: Int, c: Int, matrix: Matrix): Seq[(Int, Int)] =
    Seq((r - 1, c), (r + 1, c), (r, c - 1), (r, c + 1)).filter((nr, nc) => matrix.inBounds(nr, nc))

  private def growCluster(matrix: Matrix, seed: (Int, Int), clusterSize: Int, growthProbability: Double, growthType: CellType): Matrix = {

    @tailrec
    def expand(queue: Seq[(Int, Int)], visited: Set[(Int, Int)], count: Int, m: Matrix): Matrix =
      if count >= clusterSize || queue.isEmpty then m
      else {
        val (r, c) = queue.head
        val newMatrix = m.update(r, c, Cell(r, c, growthType, CellState.Intact))
        val next = neighbors(r, c, newMatrix)
          .filterNot(visited.contains)
          .filter((r, c) => m(r, c).cellType == CellType.Empty)
          .filter(_ => random.nextDouble() < growthProbability)
        expand(queue.tail ++ next, visited + ((r, c)), count + 1, newMatrix)
      }

    expand(Seq(seed), Set.empty, 0, matrix)
  }
}