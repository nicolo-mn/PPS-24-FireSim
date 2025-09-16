package it.unibo.firesim.model.map

import it.unibo.firesim.model.fire.FireStage.Ignition
import it.unibo.firesim.model.map.CellType.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MatrixTest extends AnyFlatSpec with Matchers:
  "Matrix" should "return correct number of rows" in {
    val matrix =
      Vector.tabulate(3, 3)((row, col) => Forest)
    matrix.rows should be(3)
  }

  it should "return correct number of cols" in {
    val matrix =
      Vector.tabulate(3, 3)((row, col) => Forest)
    matrix.cols should be(3)
  }

  it should "return the correct cell for each position" in {
    val matrix =
      Vector.tabulate(3, 3)((row, col) => Forest)
    matrix(0)(0) should be(Forest)
  }

  it should "update cells correctly" in {
    val matrix = Vector.tabulate(3, 3)((row, col) =>
      Forest
    ).update(0, 0, Station)
    matrix(0)(0) should be(Station)
  }

  it should "get the positions of cells with a certain cell type" in {
    val matrix = Vector.tabulate(3, 3)((row, col) =>
      Forest
    ).update(
      0,
      0,
      Station
    ).update(1, 1, Station).update(2, 2, Station)
    matrix.positionsOf(Station) should be(Seq((0, 0), (1, 1), (2, 2)))
  }

  it should "get the positions of cells with a certain problematic cell type" in {
    val matrix = Vector.tabulate(3, 3)((row, col) =>
      Forest
    ).update(
      0,
      0,
      Burning(10, Ignition, Grass)
    ).update(
      1,
      1,
      Burning(1, Ignition, Grass)
    ).update(2, 2, Burning(3, Ignition, Grass))
    matrix.positionsOf(Burning(
      10,
      Ignition,
      Grass
    )) should be(Seq((0, 0)))
    matrix.positionsOf {
      case Burning(_, _, _) => true
      case _                => false
    } should be(Seq((0, 0), (1, 1), (2, 2)))
  }

  it should "get the positions of the neighboring cells" in {
    val matrix = Vector.tabulate(3, 3)((row, col) => Forest)

    matrix.neighbors(1, 1) should contain allElementsOf Seq(
      (0, 0),
      (1, 0),
      (2, 0),
      (0, 2),
      (1, 2),
      (2, 2),
      (0, 1),
      (2, 1)
    )
  }
