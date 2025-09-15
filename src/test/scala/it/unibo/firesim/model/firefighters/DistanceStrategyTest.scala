package it.unibo.firesim.model.firefighters

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DistanceStrategyTest extends AnyFlatSpec with Matchers:

  "euclideanDistance" should "return 0.0 for identical points" in {
    DistanceStrategy.euclideanDistance((0, 0), (0, 0)) should be(0.0)
  }

  it should "return correct distance on the X axis" in {
    DistanceStrategy.euclideanDistance((0, 0), (3, 0)) should be(3.0)
    DistanceStrategy.euclideanDistance((5, 0), (2, 0)) should be(3.0)
  }

  it should "return correct distance on the Y axis" in {
    DistanceStrategy.euclideanDistance((0, 0), (0, 4)) should be(4.0)
    DistanceStrategy.euclideanDistance((0, -2), (0, 1)) should be(3.0)
  }

  it should "return correct diagonal distance" in {
    DistanceStrategy.euclideanDistance((0, 0), (3, 4)) should be(5.0)
    DistanceStrategy.euclideanDistance((1, 1), (4, 5)) should be(5.0)
  }

  it should "be symmetric regardless of argument order" in {
    val d1 = DistanceStrategy.euclideanDistance((2, 3), (5, 7))
    val d2 = DistanceStrategy.euclideanDistance((5, 7), (2, 3))
    d1 should be(d2)
  }
