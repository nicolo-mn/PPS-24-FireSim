package it.unibo.firesim.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CellTest extends AnyFlatSpec with Matchers {

  "Cell" should "be created with correct position, type and state" in {
    val cell = Cell(0, 1, CellType.Forest, CellState.Intact)
    cell.x should be(0)
    cell.y should be(1)
    cell.cellType should be(CellType.Forest)
    cell.state should be(CellState.Intact)
  }
  
  it should "keep track of the burning start cycle" in {
    val cell = Cell(0, 1, CellType.Forest, CellState.Burning(4))
    cell.state should be(CellState.Burning(4))
    cell.state shouldNot be(CellState.Burning(5))
  }
}