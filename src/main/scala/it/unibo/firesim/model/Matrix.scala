package it.unibo.firesim.model

import it.unibo.firesim.model.cell.Cell

/** Represents a matrix of cells.
  *
  * @param cells
  *   2D array of cells
  */
case class Matrix(cells: Vector[Vector[Cell]]):
  val rows: Int = cells.length
  val cols: Int = if cells.isEmpty then 0 else cells(0).length

  /** @param r
    *   Row index
    * @param c
    *   Column index
    * @return
    *   The cell at the specified position (r,c)
    */
  def apply(r: Int, c: Int): Cell = cells(r)(c)

  /** @param r
    *   Row index
    * @param c
    *   Column index
    * @param newCell
    *   The new cell to place at (r, c)
    * @return
    *   A new Matrix with the updated cell
    */
  def update(r: Int, c: Int, newCell: Cell): Matrix =
    Matrix(cells.updated(r, cells(r).updated(c, newCell)))

  /** @param r
    *   Row index
    * @param c
    *   Column index
    * @return
    *   True if the indices (r, c) are within the bounds of the matrix
    */
  def inBounds(r: Int, c: Int): Boolean =
    r >= 0 && r < rows && c >= 0 && c < cols
