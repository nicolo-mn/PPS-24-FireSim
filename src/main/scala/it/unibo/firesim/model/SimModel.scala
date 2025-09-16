package it.unibo.firesim.model

import it.unibo.firesim.model.map.CellType.*
import it.unibo.firesim.model.fire.*
import it.unibo.firesim.config.Config.*
import it.unibo.firesim.util.{RNG, SimpleRNG}
import it.unibo.firesim.model.firefighters.FireFighterState.*
import it.unibo.firesim.model.firefighters.FireFighter
import it.unibo.firesim.model.firefighters.builder.FireFighterDSL.*
import it.unibo.firesim.model.map.MapBuilderDSL.*
import it.unibo.firesim.model.map.{CellType, MapBuilder, Matrix, neighbors, positionsOf, update}

import scala.collection.parallel.CollectionConverters.*
import scala.util.Random

given ProbabilityCalc: ProbabilityCalc =
  ProbabilityBuilder()
    .withHumidityPenalty
    .withWind
    .withWaterEffects

given BurnDurationPolicy: BurnDurationPolicy = defaultBurnDuration

class SimModel(
    random: Random = Random(),
    initial: SimParams = SimParams(20, 90, 20, 50)
) extends Model:

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
  private var rng: RNG = SimpleRNG(System.currentTimeMillis())

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
    cycle = 0
    this.rows = rows
    this.cols = cols

    matrix = buildMap(rows, cols, random):
      withWater
      withForests
      withGrass
      withStations
      withFires

    firefighters = matrix.positionsOf(Station).map(s =>
      createFireFighter:
        withRay(fireFighterRay)
        stationedIn(s)
    )

    matrix

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

    if cellType == oldCell then return

    cellType match
      case Burning(_, _, _) if oldCell != Forest && oldCell != Grass => return
      case _                                                         =>
        if oldCell == Station then
          firefighters = firefighters.filter(f => f.station != pos)
        if cellType == Station then
          firefighters = firefighters :+ createFireFighter:
            withRay(fireFighterRay)
            stationedIn(pos)
        matrix = matrix.update(r, c, cellType)

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

  private def isSavable(pos: (Int, Int)): Boolean =
    matrix.neighbors(pos._1, pos._2).exists((r, c) =>
      matrix(r)(c) == Grass || matrix(r)(c) == Forest
    )
