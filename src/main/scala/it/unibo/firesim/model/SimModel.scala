package it.unibo.firesim.model

import CellType.*
import it.unibo.firesim.model.fire.*
import it.unibo.firesim.config.Config.*
import it.unibo.firesim.util.{RNG, SimpleRNG}
import it.unibo.firesim.model.firefighters.FireFighterState.*
import it.unibo.firesim.model.firefighters.FireFighter
import it.unibo.firesim.model.firefighters.builder.FireFighterDSL.*

import scala.collection.parallel.CollectionConverters.*
import scala.annotation.tailrec
import scala.util.Random

given ProbabilityCalc: ProbabilityCalc =
  directionalWindProbabilityDynamic(defaultProbabilityCalc)

given BurnDurationPolicy: BurnDurationPolicy = defaultBurnDuration

class SimModel(
    random: Random = Random(),
    initial: SimParams = SimParams(1, 0, 25, 50)
):

  private val lock = new AnyRef
  private var params = initial
  private var matrix: Matrix = Vector.empty
  private var firefighters: Seq[FireFighter] = Seq.empty

  private val firefightersUpdater =
    for
      _ <- moveStep
      extinguished <- extinguishStep
    yield extinguished

  private var cycle: Int = 0
  private var rows, cols: Int = 0
  private var rng: RNG = SimpleRNG(42)

  class MapBuilder(private var current: Matrix):

    def addLakes(): MapBuilder =
      current = SimModel.this.addLakes(current)
      this

    def addForests(): MapBuilder =
      current = SimModel.this.addForests(current)
      this

    def addGrass(): MapBuilder =
      current = SimModel.this.addGrass(current)
      this

    def addStations(): MapBuilder =
      current = SimModel.this.addStations(current)
      this

    def result: Matrix = current

  /** Generates a map with the specified number of rows and columns.
    *
    * @param rows
    *   Number of rows in the map (height)
    * @param cols
    *   Number of columns in the map (width)
    * @return
    *   The Matrix containing the generated cells
    */
  def generateMap(rows: Int, cols: Int): Matrix =
    this.rows = rows
    this.cols = cols

    val baseMatrix = Vector.tabulate(rows, cols) { (r, c) => Rock }
    matrix = MapBuilder(baseMatrix)
      .addLakes()
      .addForests()
      .addGrass()
      .addStations()
      .result

    firefighters = matrix.positionsOf(Station).map(s =>
      createFireFighter:
        withRay(fireFighterRay)
        stationedIn(s)
    )

    matrix

  private def roundedMeanMul(ratio: Double): Int =
    (ratio * (rows + cols) / 2).round.toInt

  private def addLakes(matrix: Matrix): Matrix =
    val lakeSeedsCount = roundedMeanMul(lakeSeedFrequency) max 1
    val lakeSeeds = generateSeeds(rows, cols, lakeSeedsCount)
    val minLakeSize = roundedMeanMul(minLakeSizeRatio)
    val maxLakeSize = roundedMeanMul(maxLakeSizeRatio)
    lakeSeeds.par.foldLeft(matrix) { (m, seed) =>
      growCluster(
        m,
        seed,
        random.between(minLakeSize, maxLakeSize),
        lakeGrowthProbability,
        Water
      )
    }

  private def addForests(matrix: Matrix): Matrix =
    val forestSeedsCount = roundedMeanMul(forestSeedFrequency) max 1
    val forestSeeds = generateSeeds(rows, cols, forestSeedsCount)

    val minForestSize = roundedMeanMul(minForestSizeRatio)
    val maxForestSize = roundedMeanMul(maxForestSizeRatio)
    forestSeeds.par.foldLeft(matrix) { (m, seed) =>
      growCluster(
        m,
        seed,
        random.between(minForestSize, maxForestSize),
        forestGrowthProbability,
        Forest
      )
    }

  private def addGrass(matrix: Matrix): Matrix =
    val grassSeeds: Seq[(Int, Int)] = matrix.positionsOf(Forest).par
      .flatMap((r, c) => neighbors(r, c, matrix))
      .filter((r, c) => matrix(r)(c) == Rock)
      .seq

    val minGrassSpreadDistance = roundedMeanMul(minGrassSizeRatio)
    val maxGrassSpreadDistance = roundedMeanMul(maxGrassSizeRatio)
    grassSeeds.par.foldLeft(matrix) { (matrix, seed) =>
      growCluster(
        matrix,
        seed,
        random.between(minGrassSpreadDistance, maxGrassSpreadDistance),
        grassGrowthProbability,
        Grass
      )
    }

  private def addStations(matrix: Matrix): Matrix =
    val stationSeedsCount = roundedMeanMul(stationSeedsFrequency) max 1
    val stationSeeds =
      generateSparseSeeds(rows, cols, stationSeedsCount, matrix)
    stationSeeds.par.foldLeft(matrix)((m, pos) =>
      m.update(
        pos._1,
        pos._2,
        Station
      )
    )

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

  private def neighbors(r: Int, c: Int, matrix: Matrix): Seq[(Int, Int)] =
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

  /** @return
    *   The simulation parameters
    */
  def getSimParams: SimParams =
    lock.synchronized { params }

  /** @param f
    *   The function to update the simulation parameters
    */
  def updateParams(f: SimParams => SimParams): Unit =
    lock.synchronized { params = f(params) }

  /** @return
    *   The current cycle number
    */
  def getCurrentCycle: Int = cycle

  /** @param cells
    *   The cells to place
    * @return
    *   The updated game matrix, the list of positions of firefighters above the
    *   map
    */
  def placeCells(cells: Seq[((Int, Int), CellType)])
      : (Matrix, Seq[(Int, Int)]) =
    cells.foreach((p, cT) => placeCell(p, cT))
    (matrix, firefighters.map(f => f.position))

  private def placeCell(pos: (Int, Int), cellType: CellType): Unit =
    val (r, c) = pos
    val oldCell = matrix(r)(c)

    if oldCell == cellType || oldCell == Station then return

    cellType match
      case Burning(_, _, _) if oldCell != Forest && oldCell != Grass => return
      case _ => matrix = matrix.update(r, c, cellType)

  /** Game tick method
    * @return
    *   The updated game matrix and the list of positions of firefighters above
    *   the map
    */
  def updateState(): (Matrix, Seq[(Int, Int)]) =
    val simParams = this.getSimParams
    val burningCells = matrix.positionsOf {
      case Burning(_, _, _) => true
      case _                => false
    }.toSet
    val (updatedMatrix, updatedBurningCells, nextRandoms) =
      fireSpread(matrix, burningCells, simParams, cycle, rng)
    matrix = updatedMatrix
    rng = nextRandoms
    val actionableCells = burningCells.filter(isSavable)
    val firefightersUpdates =
      firefighters.par.map(firefightersUpdater(actionableCells, _)).seq
    firefighters = firefightersUpdates.map(_._1)
    extinguishCells(firefightersUpdates.foldLeft(Set.empty[(Int, Int)])(
      (s, u) =>
        s ++ u._2
    ).toSeq)

    cycle += 1
    (matrix, firefighters.map(f => f.position))

  private def extinguishCells(burntCells: Seq[(Int, Int)]): Unit =
    burntCells.foreach(p => placeCell(p, Burnt))

  private def generateSeeds(rows: Int, cols: Int, count: Int): Seq[(Int, Int)] =
    Seq.fill(count)((random.nextInt(rows), random.nextInt(cols)))

  private def isSavable(pos: (Int, Int)): Boolean =
    neighbors(pos._1, pos._2, matrix).exists((r, c) =>
      matrix(r)(c) == Grass || matrix(r)(c) == Forest
    )
