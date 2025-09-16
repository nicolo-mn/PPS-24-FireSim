package it.unibo.firesim.model

import it.unibo.firesim.model.map.{CellType, Matrix}

trait Model:

  /** Generates a map with the specified number of rows and columns.
    *
    * @param rows
    *   Number of rows in the map (height)
    * @param cols
    *   Number of columns in the map (width)
    * @return
    *   The Matrix containing the generated cells
    */
  def generateMap(rows: Int, cols: Int): Matrix

  /** @return
    *   The simulation parameters
    */
  def getSimParams: SimParams

  /** @param f
    *   The function to update the simulation parameters
    */
  def updateParams(f: SimParams => SimParams): Unit

  /** @return
    *   The current cycle number
    */
  def getCurrentCycle: Int

  /** @param cells
    *   The cells to place
    * @return
    *   The updated game matrix, the list of positions of firefighters above the
    *   map
    */
  def placeCells(cells: Seq[((Int, Int), CellType)]): (Matrix, Seq[(Int, Int)])

  /** Game tick method
    * @return
    *   The updated game matrix and the list of positions of firefighters above
    *   the map
    */
  def updateState(): (Matrix, Seq[(Int, Int)])
