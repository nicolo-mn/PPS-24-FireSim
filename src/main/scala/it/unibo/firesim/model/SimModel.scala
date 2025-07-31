package it.unibo.firesim.model

import it.unibo.firesim.model.cell.CellState.{Burnt, Intact}
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

    val minForestSize = 10
    val maxForestSize = 50
    val withForests = forestSeeds.foldLeft(matrix) { (matrix, seed) =>
      growForestCluster(matrix, seed, random.between(minForestSize, maxForestSize))
    }

    val withGrass = growGrassAroundForests(withForests)

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
      .filter((r, c) => matrix(r,c).cellType == CellType.Empty)
      .distinct
      .take(count)
      .toList
  }

  private def growForestCluster(matrix: Matrix, seed: (Int, Int), clusterSize: Int): Matrix = {

    def neighbors(r: Int, c: Int): Seq[(Int, Int)] =
      Seq((r - 1, c), (r + 1, c), (r, c - 1), (r, c + 1)).filter((nr, nc) => matrix.inBounds(nr, nc))

    @tailrec
    def expand(queue: Seq[(Int, Int)], visited: Set[(Int, Int)], count: Int, m: Matrix): Matrix =
      if count >= clusterSize || queue.isEmpty then m
      else {
        val (r, c) = queue.head
        val newMatrix = m.update(r, c, Cell(r, c, CellType.Forest, CellState.Intact))
        val next = neighbors(r, c).filterNot(visited.contains).filter((r, c) => m(r, c).cellType == CellType.Empty)
        expand(queue.tail ++ next, visited + ((r,c)), count + 1, newMatrix)
      }

    expand(Seq(seed), Set.empty, 0, matrix)
  }

  private def growGrassAroundForests(matrix: Matrix): Matrix = {

    val newCells = for {
      r <- 0 until matrix.rows
      c <- 0 until matrix.cols
      if matrix(r, c).cellType == CellType.Forest
      (nr, nc) <- (-1 to 1).flatMap(dr => (-1 to 1).map(dc => (r + dr, c + dc)))
      if matrix.inBounds(nr, nc) && matrix(nr, nc).cellType == CellType.Empty
    } yield (nr, nc)

    newCells.distinct.foldLeft(matrix) { case (m, (r, c)) =>
      m.update(r, c, Cell(r, c, CellType.Grass, CellState.Intact))
    }
  }
}