package it.unibo.firesim.model.cell

/**
 * Represents a cell in the simulation grid.
 * @param x the x-coordinate of the cell
 * @param y the y-coordinate of the cell
 * @param cellType the type of the cell
 * @param state the current state of the cell
 */
case class Cell(
  x: Int,
  y: Int,
  cellType: CellType,
  state: CellState
) {

  /** @return the position of the cell as a tuple (x, y). */
  def position: (Int, Int) = (x, y)

}