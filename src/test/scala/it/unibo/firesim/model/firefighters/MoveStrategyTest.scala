package it.unibo.firesim.model.firefighters

import it.unibo.firesim.model.firefighters.MoveStrategy.bresenham
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MoveStrategyTest extends AnyFlatSpec with Matchers:

  "Bresenham algorithm" should "generate a straight horizontal path" in {
    val path = bresenham((0, 0), (3, 0)).take(5).toList
    path shouldBe List(
      (0, 0),
      (1, 0),
      (2, 0),
      (3, 0),
      (3, 0)
    ) // repeats last cell
  }

  it should "generate a straight vertical path" in {
    val path = bresenham((0, 0), (0, 3)).take(5).toList
    path shouldBe List((0, 0), (0, 1), (0, 2), (0, 3), (0, 3))
  }

  it should "generate a diagonal path" in {
    val path = bresenham((0, 0), (3, 3)).take(5).toList
    path shouldBe List((0, 0), (1, 1), (2, 2), (3, 3), (3, 3))
  }

  it should "generate a shallow slope path" in {
    val path = bresenham((0, 0), (5, 2)).take(7).toList
    path shouldBe List((0, 0), (1, 0), (2, 1), (3, 1), (4, 2), (5, 2), (5, 2))
  }

  it should "generate a steep slope path" in {
    val path = bresenham((0, 0), (2, 5)).take(7).toList
    path shouldBe List((0, 0), (0, 1), (1, 2), (1, 3), (2, 4), (2, 5), (2, 5))
  }

  it should "work when moving backwards horizontally" in {
    val path = bresenham((3, 0), (0, 0)).take(5).toList
    path shouldBe List((3, 0), (2, 0), (1, 0), (0, 0), (0, 0))
  }

  it should "work when moving backwards vertically" in {
    val path = bresenham((0, 3), (0, 0)).take(5).toList
    path shouldBe List((0, 3), (0, 2), (0, 1), (0, 0), (0, 0))
  }

  it should "work when moving diagonally backwards" in {
    val path = bresenham((3, 3), (0, 0)).take(5).toList
    path shouldBe List((3, 3), (2, 2), (1, 1), (0, 0), (0, 0))
  }

  it should "return the same point if from == to" in {
    val path = bresenham((2, 2), (2, 2)).take(3).toList
    path shouldBe List((2, 2), (2, 2), (2, 2))
  }
