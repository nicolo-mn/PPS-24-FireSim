package it.unibo.firesim.model

import it.unibo.firesim.model.cell.{CellState, CellType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SimModelTest extends AnyFlatSpec with Matchers {

  "SimModel" should "generate the map with correct dimensions" in {
    val model = SimModel()
    val matrix = model.generateMap(500, 100)

    matrix.rows should be(500)
    matrix.cols should be(100)
  }

  it should "generate a map with a correct diversity of cell types" in {
    val model = SimModel()
    val matrix = model.generateMap(100, 100)

    val cellTypes = matrix.cells.flatten.map(_.cellType).distinct
    val cellStates = matrix.cells.flatten.map(_.state).distinct

    cellTypes should contain allElementsOf CellType.values.toSeq
    cellStates should be (CellState.Intact)
  }
}