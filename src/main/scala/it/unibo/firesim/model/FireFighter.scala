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
  private val directions: Seq[(Int, Int)] = adjacentCellsInRay(1, true)
  private val actionableCells = adjacentCellsInRay(fireFighterRay, true)

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
    directions.map(d => (d._1 + position._1, d._2 + position._2)).filter(p =>
      0 <= p._1 && p._1 < rows && 0 <= p._2 && p._2 < cols
    )
      .minBy(distance(_, target))

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

  private def adjacentCellsInRay(
      ray: Int,
      includeCenter: Boolean
  ): Seq[(Int, Int)] =
    for
      dr <- -ray to ray
      dc <- -ray to ray
      if includeCenter || (dr != 0 || dc != 0) // handle center
    yield (dr, dc)
