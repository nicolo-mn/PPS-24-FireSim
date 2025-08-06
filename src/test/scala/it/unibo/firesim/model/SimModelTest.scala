package it.unibo.firesim.model

import it.unibo.firesim.model.cell.CellType
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SimModelTest extends AnyFlatSpec with Matchers:

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

    cellTypes should contain allElementsOf Seq(
      CellType.Forest,
      CellType.Grass,
      CellType.Station
    ) // Empty not included
  }

  it should "generate a small map with at least one fire station and one forest" in {
    val model = SimModel()
    val matrix = model.generateMap(5, 5)

    val fireStations =
      matrix.cells.flatten.count(_.cellType == CellType.Station)
    val forests = matrix.cells.flatten.count(_.cellType == CellType.Forest)

    fireStations should be > 0
    forests should be > 0
  }

  it should "generate a map with low percentage of empty cells" in {
    val model = SimModel()
    val matrix = model.generateMap(100, 100)

    val emptyCellsCount =
      matrix.cells.flatten.count(_.cellType == CellType.Empty)
    val totalCellsCount = matrix.rows * matrix.cols

    emptyCellsCount should be < (totalCellsCount * 0.1).toInt
  }

  it should "Impostazione e recupero dei parametri funziona" in {
    val model = new SimModel()
    model.setWindSpeed(5.0)
    model.setHumidity(75.0)
    val params = model.getSimParams
    assert(params.windSpeed == 5.0)
    assert(params.humidity == 75.0)
  }
