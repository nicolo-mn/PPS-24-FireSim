package it.unibo.firesim.model.cell

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CellTest extends AnyFlatSpec with Matchers:

  "Cell" should "be created with correct position, type and state" in {
    val cell = Cell(0, 1, CellType.Forest)
    cell.row should be(0)
    cell.col should be(1)
    cell.position should be((0, 1))
    cell.cellType should be(CellType.Forest)

  }

  it should "keep track of the burning start cycle" in {
    val cell = Cell(0, 1, CellType.Burning(4))
    cell.cellType should be(CellType.Burning(4))
    cell.cellType shouldNot be(CellType.Burning(5))
  }
