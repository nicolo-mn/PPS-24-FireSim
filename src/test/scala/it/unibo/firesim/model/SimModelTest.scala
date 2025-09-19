package it.unibo.firesim.model

import it.unibo.firesim.model.fire.FireStage.Ignition
import it.unibo.firesim.model.map.{CellType, cols, positionsOf, positionsOfBurning, rows}
import it.unibo.firesim.model.map.CellType.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SimModelTest extends AnyFlatSpec with Matchers:

  private val model = SimModel()

  "SimModel" should "generate the map with correct dimensions" in {
    val matrix = model.generateMap(500, 100)

    matrix.rows should be(500)
    matrix.cols should be(100)
  }

  it should "generate a map with a correct diversity of cell types" in {
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
    val matrix = model.generateMap(100, 100)

    val emptyCellsCount =
      matrix.flatten.count(_ == Rock)
    val totalCellsCount = matrix.rows * matrix.cols

    emptyCellsCount should be < (totalCellsCount * 0.1).toInt
  }

  it should "update and get simulation parameters" in {
    model.updateParams(_.copy(windSpeed = 5))
    model.updateParams(_.copy(humidity = 75))
    val params = model.getSimParams
    assert(params.windSpeed == 5)
    assert(params.humidity == 75)
  }

  it should "update the cycle of simulation correctly for every update" in {
    model.getCurrentCycle shouldEqual 0
    model.updateState()
    model.getCurrentCycle shouldEqual 1
  }

  it should "place cells in matrix correctly, and not placing them again if already placed" in {
    val matrix = model.generateMap(10, 10)
    matrix.positionsOf(Empty) should be(Seq.empty)

    val (updatedMatrix, _) =
      model.placeCells(Seq(((0, 0), Empty), ((1, 1), Empty)))
    val (notUpdatedMatrix, _) = model.placeCells(Seq(((0, 0), Empty)))

    updatedMatrix.positionsOf(Empty) should be(Seq((0, 0), (1, 1)))
    notUpdatedMatrix should be(updatedMatrix)
  }

  it should "place burning cells only if the cell in that position is Forest or Grass and with a valid start cycle" in {
    model.generateMap(10, 10)
    val (matrix, _) = model.placeCells(Seq(((0, 0), Empty)))
    val burningForest = Burning(0, Ignition, Forest)
    val burningGrass = Burning(0, Ignition, Grass)
    val (notUpdatedMatrix, _) = model.placeCells(Seq(((0, 0), burningForest)))
    notUpdatedMatrix should be(matrix)

    val (withForest, _) = model.placeCells(Seq(((0, 0), Forest)))
    val (notUpdatedWithForest, _) =
      model.placeCells(Seq(((0, 0), Burning(-1, Ignition, Forest))))
    val (alsoNotUpdatedWithForest, _) =
      model.placeCells(Seq(((0, 0), burningGrass)))
    val (updatedWithForest, _) = model.placeCells(Seq(((0, 0), burningForest)))
    notUpdatedWithForest should be(withForest)
    alsoNotUpdatedWithForest should be(withForest)
    updatedWithForest shouldNot be(withForest)

    val (withGrass, _) = model.placeCells(Seq(((0, 0), Grass)))
    val (notUpdatedWithGrass, _) =
      model.placeCells(Seq(((0, 0), Burning(-1, Ignition, Grass))))
    val (alsoNotUpdatedWithGrass, _) =
      model.placeCells(Seq(((0, 0), burningForest)))
    val (updatedWithGrass, _) = model.placeCells(Seq(((0, 0), burningGrass)))
    notUpdatedWithGrass should be(withGrass)
    alsoNotUpdatedWithGrass should be(withGrass)
    updatedWithGrass shouldNot be(withGrass)
  }

  it should "add a new firefighter if a station is placed" in {
    model.generateMap(10, 10)
    val (matrix, firefighters) = model.placeCells(Seq(((0, 0), Empty)))
    val (updatedMatrix, updatedFirefighters) =
      model.placeCells(Seq(((0, 0), Station)))
    updatedMatrix shouldNot be(matrix)
    updatedFirefighters.length should be(firefighters.length + 1)
  }

  it should "remove a firefighter if a station is replaced with another cell" in {
    model.generateMap(10, 10)
    val (matrix, firefighters) = model.placeCells(Seq(((0, 0), Station)))
    val (updatedMatrix, updatedFirefighters) =
      model.placeCells(Seq(((0, 0), Empty)))
    updatedMatrix shouldNot be(matrix)
    updatedFirefighters.length should be(firefighters.length - 1)
  }

  it should "update state correctly, changing the matrix and the firefighters position" in {
    model.generateMap(10, 10)
    val matrix, firefighters = model.updateState()
    for i <- 1 to 100 do
      model.updateState()
    val (updatedMatrix, updatedFirefighters) = model.updateState()

    updatedMatrix shouldNot be(matrix)
    updatedFirefighters shouldNot be(firefighters)
  }
