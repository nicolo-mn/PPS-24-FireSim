package it.unibo.firesim.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChebyshevDistanceTest extends AnyFlatSpec with Matchers:

  "chebyshevDistance" should "return 0 for identical points" in {
    ChebyshevDistance.distance((0, 0), (0, 0)) should be(0)
  }

  it should "return correct distance on the X axis" in {
    ChebyshevDistance.distance((0, 0), (3, 0)) should be(3)
    ChebyshevDistance.distance((5, 0), (2, 0)) should be(3)
  }

  it should "return correct distance on the Y axis" in {
    ChebyshevDistance.distance((0, 0), (0, 4)) should be(4)
    ChebyshevDistance.distance((0, -2), (0, 1)) should be(3)
  }

  it should "return correct diagonal distance" in {
    ChebyshevDistance.distance((0, 0), (3, 4)) should be(4)
    ChebyshevDistance.distance((1, 1), (4, 5)) should be(4)
  }

  it should "be symmetric regardless of argument order" in {
    val d1 = ChebyshevDistance.distance((2, 3), (5, 7))
    val d2 = ChebyshevDistance.distance((5, 7), (2, 3))
    d1 should be(d2)
  }
