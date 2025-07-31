package it.unibo.firesim.model

import it.unibo.firesim.model.cell.Cell

/** Represents a matrix of cells.
 *
 * @param cells 2D array of cells
 */
case class Matrix(cells: Array[Array[Cell]]) {
  val rows: Int = cells.length
  val cols: Int = if (cells.isEmpty) 0 else cells(0).length
  def apply(row: Int, col: Int): Cell = cells(row)(col)
}