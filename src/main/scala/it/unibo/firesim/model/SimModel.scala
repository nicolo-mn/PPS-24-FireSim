package it.unibo.firesim.model

import it.unibo.firesim.model.cell.{Cell, CellType, CellState}

case class Matrix(cells: Array[Array[Cell]]) {
  val rows: Int = cells.length
  val cols: Int = if (cells.isEmpty) 0 else cells(0).length
  def apply(row: Int, col: Int): Cell = cells(row)(col)
}

class SimModel {
  def generateMap(rows: Int, cols: Int): Matrix = {
    Matrix(
      Array.tabulate(rows, cols) { (row, col) =>
        Cell(row, col, CellType.Empty, CellState.Intact)
      }
    )
  }
}