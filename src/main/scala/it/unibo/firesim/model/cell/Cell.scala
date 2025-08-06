package it.unibo.firesim.model.cell

/** Represents a cell in the simulation grid.
  * @param row
  *   the row of the cell
  * @param col
  *   the column of the cell
  * @param cellType
  *   the type of the cell
  */
case class Cell(
    row: Int,
    col: Int,
    cellType: CellType
):

  /** @return the position of the cell as a tuple (x, y). */
  def position: (Int, Int) = (row, col)
