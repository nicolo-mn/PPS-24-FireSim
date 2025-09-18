package it.unibo.firesim.model

import it.unibo.firesim.model.map.{CellType, cols, rows, positionsOfBurning}
import it.unibo.firesim.model.map.CellType.*
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

    val cellTypes = matrix.flatten.distinct

    cellTypes should contain allElementsOf Seq(
      Forest,
      Grass,
      Station,
      Water
    ) // Empty not included
  }

  it should "generate a small map with at least one fire station, one forest and one fire" in {
    val model = SimModel()
    val matrix = model.generateMap(5, 5)

    val fireStations =
      matrix.flatten.count(_ == Station)
    val forests = matrix.flatten.count(_ == Forest)
    val fires = matrix.positionsOfBurning().length

    fireStations should be > 0
    forests should be > 0
    fires should be > 0
  }

  it should "generate a map with low percentage of rock cells" in {
    val model = SimModel()
    val matrix = model.generateMap(100, 100)

    val emptyCellsCount =
      matrix.flatten.count(_ == Rock)
    val totalCellsCount = matrix.rows * matrix.cols

    emptyCellsCount should be < (totalCellsCount * 0.1).toInt
  }

  it should "set and get simulation parameters" in {
    val model = new SimModel()
    model.updateParams(_.copy(windSpeed = 5))
    model.updateParams(_.copy(humidity = 75))
    val params = model.getSimParams
    assert(params.windSpeed == 5)
    assert(params.humidity == 75)
  }
