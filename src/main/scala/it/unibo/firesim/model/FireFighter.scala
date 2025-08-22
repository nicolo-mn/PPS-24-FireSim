package it.unibo.firesim.model

import it.unibo.firesim.config.Config.fireFighterRay

import scala.language.postfixOps

/** FireFighter companion object
  */
object FireFighter:

  /** Creates a FireFighter
    * @param rows
    *   number of rows of the map
    * @param cols
    *   number of columns of the map
    * @param station
    *   position of the firefighter's fire station
    * @return
    *   a FireFighter instance
    */
  def apply(rows: Int, cols: Int, station: (Int, Int)): FireFighter =
    new FireFighter(rows, cols, station)

/** Represents the output of a firefighter action
  * @param position
  *   new firefighter position
  * @param extinguishedCells
  *   extinguished cells by the current action
  */
case class FireFighterUpdate(
    position: (Int, Int),
    extinguishedCells: Seq[(Int, Int)]
)

/** Firefighter unit that extinguishes fire cells, returning to its fire station
  * after every operation to reload
  */
class FireFighter(
    private val rows: Int,
    private val cols: Int,
    private val station: (Int, Int)
):

  private var loaded = true
  private var position = station
  private var currentTarget = station
  private var err = 0
  private var deltaX = 0
  private var deltaY = 0
  private var stepX = 0
  private var stepY = 0
  private val actionableCells = adjacentCellsInRay(fireFighterRay)

  /** Moves the firefighter towards the closest fire or the station in case it
    * needs to reload, extinguishing cells when on a cell on fire
    * @param burningCells
    *   cells currently on fire
    * @return
    *   a FireFighter update containing its new position and extinguished cells
    */
  def act(burningCells: Seq[(Int, Int)]): FireFighterUpdate =
    position = if loaded && burningCells.nonEmpty then
      val targetFire = burningCells.minBy(f =>
        (distance(f, position), -burningCellsAround(f, burningCells).length)
      )
      nextStepTowards(targetFire)
    else
      nextStepTowards(station)

    var extinguishedCells = Seq.empty[(Int, Int)]
    if loaded && burningCells.contains(position) then
      loaded = false
      extinguishedCells = burningCellsAround(position, burningCells)
    else if !loaded && position == station then
      loaded = true
    FireFighterUpdate(position, extinguishedCells)

  private def nextStepTowards(target: (Int, Int)): (Int, Int) =
    if currentTarget != target then
      currentTarget = target
      initBresenhamAlgorithm(target)

    if position._1 == target._1 && position._2 == target._2 then
      position
    else
      var (nx, ny) = position
      val e2 = 2 * err
      if e2 >= deltaY then
        err += deltaY
        nx += stepX
      if e2 <= deltaX then
        err += deltaX
        ny += stepY
      (nx, ny)

  private def initBresenhamAlgorithm(target: (Int, Int)): Unit =
    val (x0, y0) = position
    val (x1, y1) = target
    deltaX = math.abs(x1 - x0)
    deltaY = -math.abs(y1 - y0)
    stepX = if x0 < x1 then 1 else -1
    stepY = if y0 < y1 then 1 else -1
    err = deltaX + deltaY

  private def burningCellsAround(
      pos: (Int, Int),
      currentBurningCells: Seq[(Int, Int)]
  ): Seq[(Int, Int)] =
    currentBurningCells.filter(actionableCells.map(d =>
      (d._1 + pos._1, d._2 + pos._2)
    ).contains)

  private def distance(p1: (Int, Int), p2: (Int, Int)): Int =
    // Chebyshev distance as 8-direction movements are allowed
    math.max(math.abs(p1._1 - p2._1), math.abs(p1._2 - p2._2))

  private def adjacentCellsInRay(ray: Int): Seq[(Int, Int)] =
    for
      dr <- -ray to ray
      dc <- -ray to ray
    yield (dr, dc)
