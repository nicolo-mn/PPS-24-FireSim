package it.unibo.firesim.model

import it.unibo.firesim.model.cell.Cell

/** Represents a matrix of cells.
 *
 * @param cells 2D array of cells
 */
case class Matrix(cells: Vector[Vector[Cell]]) {
  val rows: Int = cells.length
  val cols: Int = if (cells.isEmpty) 0 else cells(0).length
  
  def apply(r: Int, c: Int): Cell = cells(r)(c)

  def update(r: Int, c: Int, newCell: Cell): Matrix =
    Matrix(cells.updated(r, cells(r).updated(c, newCell)))
}