package it.unibo.firesim.model

import it.unibo.firesim.model.cell.Cell

type Matrix = Vector[Vector[Cell]]

extension (matrix: Matrix)
  /** Matrix rows
    * @return
    *   number of rows in the matrix
    */
  def rows: Int = matrix.length

  /** Matrix columns
    * @return
    *   number of columns in the matrix
    */
  def cols: Int = if matrix.nonEmpty then matrix.head.length else 0

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
    matrix.updated(r, matrix(r).updated(c, newCell))

  /** @param r
    *   Row index
    * @param c
    *   Column index
    * @return
    *   True if the indices (r, c) are within the bounds of the matrix
    */
  def inBounds(r: Int, c: Int): Boolean =
    r >= 0 && r < matrix.length &&
      c >= 0 && c < matrix(r).length
