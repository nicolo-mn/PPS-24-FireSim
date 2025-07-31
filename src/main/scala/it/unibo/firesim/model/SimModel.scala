package it.unibo.firesim.model

import it.unibo.firesim.model.cell.{Cell, CellType, CellState}

class SimModel {

  /** Generates a map with the specified number of rows and columns.
   *
   * @param rows Number of rows in the map (height)
   * @param cols Number of columns in the map (width)
   * @return A Matrix containing the generated cells
   */
  def generateMap(rows: Int, cols: Int): Matrix = {
    Matrix(
      Vector.tabulate(rows, cols) { (row, col) =>
        Cell(row, col, CellType.Empty, CellState.Intact)
      }
    )
  }
}