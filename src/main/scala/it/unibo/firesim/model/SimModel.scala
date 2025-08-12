package it.unibo.firesim.model

import it.unibo.firesim.model.cell.CellType
import it.unibo.firesim.model.cell.CellType.*

import scala.annotation.tailrec
import scala.util.Random

class SimModel(
    random: Random = Random(),
    private var simParams: SimParams = SimParams(1.0, 0.0, 25.0, 50.0)
):

  private var matrix: Matrix = Vector.empty
  private var firefighters: Seq[(Int, Int)] = Seq.empty

  /** Generates a map with the specified number of rows and columns.
    *
    * @param rows
    *   Number of rows in the map (height)
    * @param cols
    *   Number of columns in the map (width)
    * @return
    *   A Matrix containing the generated cells
    */
  def generateMap(rows: Int, cols: Int): Matrix =
    val matrix = Vector.tabulate(rows, cols) { (r, c) =>
      Rock
    }

    val forestSeedFrequency = 0.02 // 2%
    val forestSeedsCount = ((rows * cols) * forestSeedFrequency).toInt max 1
    val forestSeeds = generateSeeds(rows, cols, forestSeedsCount)

    val minForestSize = 30
    val maxForestSize = 100
    val forestGrowthProbability = 0.7 // 70%
    val withForests = forestSeeds.foldLeft(matrix) { (matrix, seed) =>
      growCluster(
        matrix,
        seed,
        random.between(minForestSize, maxForestSize),
        forestGrowthProbability,
        Forest
      )
    }

    val grassSeeds: Seq[(Int, Int)] =
      (0 until withForests.rows).flatMap { r =>
        (0 until withForests.cols).flatMap { c =>
          if withForests(r)(c) == Forest then
            neighbors(r, c, withForests).filter { case (nr, nc) =>
              withForests(nr)(nc) == Rock
            }
          else
            Seq.empty
        }
      }

    val minGrassSpreadDistance = 10
    val maxGrassSpreadDistance = 40
    val grassGrowthFrequency = 0.8 // 80%
    val withGrass = grassSeeds.foldLeft(withForests) { (matrix, seed) =>
      growCluster(
        matrix,
        seed,
        random.between(minGrassSpreadDistance, maxGrassSpreadDistance),
        grassGrowthFrequency,
        Grass
      )
    }

    val stationSeedsFrequency = 0.0002 // 0.02%
    val stationSeedsCount = ((rows * cols) * stationSeedsFrequency).toInt max 1
    val stationSeeds =
      generateSparseSeeds(rows, cols, stationSeedsCount, withGrass)
    val withStations = stationSeeds.foldLeft(withGrass)((m, pos) =>
      m.update(
        pos._1,
        pos._2,
        Station
      )
    )

    this.matrix = withStations
    withStations

  private def generateSparseSeeds(
      rows: Int,
      cols: Int,
      count: Int,
      matrix: Matrix
  ): Seq[(Int, Int)] =
    val emptyCells = (0 until rows).flatMap { r =>
      (0 until cols).collect {
        case c if matrix(r)(c) == Rock => (r, c)
      }
    }
    if emptyCells.isEmpty then
      return Seq((random.nextInt(rows), random.nextInt(cols)))
    random.shuffle(emptyCells).take(count)

  def neighbors(r: Int, c: Int, matrix: Matrix): Seq[(Int, Int)] =
    Seq((r - 1, c), (r + 1, c), (r, c - 1), (r, c + 1)).filter((nr, nc) =>
      matrix.inBounds(nr, nc)
    )

  private def growCluster(
      matrix: Matrix,
      seed: (Int, Int),
      clusterSize: Int,
      growthProbability: Double,
      growthType: CellType
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
        val next = neighbors(r, c, newMatrix)
          .filterNot(visited.contains)
          .filter((r, c) => m(r)(c) == Rock)
          .filter(_ => random.nextDouble() < growthProbability)
        expand(queue.tail ++ next, visited + ((r, c)), count + 1, newMatrix)

    expand(Seq(seed), Set.empty, 0, matrix)

  def getSimParams: SimParams = simParams

  def setWindSpeed(speed: Double): Unit =
    simParams = simParams.copy(windSpeed = speed)

  def setWindAngle(angle: Double): Unit =
    simParams = simParams.copy(windAngle = angle)

  def setTemperature(temp: Double): Unit =
    simParams = simParams.copy(temperature = temp)

  def setHumidity(humidity: Double): Unit =
    simParams = simParams.copy(humidity = humidity)

  def placeCells(cells: Seq[((Int, Int), CellType)]): (Matrix, Seq[(Int, Int)]) =
    cells.foreach((p, cT) => placeCell(p, cT))
    (matrix, firefighters)

  private def placeCell(pos: (Int, Int), cellType: CellType): Unit =
    val (r, c) = pos
    val oldCell = matrix(r)(c)

    if oldCell == cellType || oldCell == Station then return

    cellType match
      case Burning(_) | Burnt => if oldCell == Forest || oldCell == Grass then matrix = matrix.update(r, c, cellType)
      case _ => matrix = matrix.update(r, c, cellType)


  def updateState(params: SimParams): (Matrix, Seq[(Int, Int)]) = ???

  def extinguishCells(burntCells: Seq[(Int, Int)]): Unit = ???

  private def generateSeeds(rows: Int, cols: Int, count: Int): Seq[(Int, Int)] =
    Seq.fill(count)((random.nextInt(rows), random.nextInt(cols)))
