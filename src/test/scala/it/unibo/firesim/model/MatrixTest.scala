package it.unibo.firesim.model

import it.unibo.firesim.model.cell.{Cell, CellType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MatrixTest extends AnyFlatSpec with Matchers:
  "Matrix" should "return correct number of rows" in {
    val matrix =
      Vector.tabulate(3, 3)((row, col) => Cell(row, col, CellType.Forest))
    matrix.rows should be(3)
  }

  it should "return correct number of cols" in {
    val matrix =
      Vector.tabulate(3, 3)((row, col) => Cell(row, col, CellType.Forest))
    matrix.cols should be(3)
  }

  it should "return the correct cell for each position" in {
    val matrix =
      Vector.tabulate(3, 3)((row, col) => Cell(row, col, CellType.Forest))
    matrix(0)(0) should be(Cell(0, 0, CellType.Forest))
  }

  it should "update cells correctly" in {
    val matrix = Vector.tabulate(3, 3)((row, col) =>
      Cell(row, col, CellType.Forest)
    ).update(0, 0, Cell(0, 0, CellType.Station))
    matrix(0)(0) should be(Cell(0, 0, CellType.Station))
  }
