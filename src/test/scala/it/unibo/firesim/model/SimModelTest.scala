package it.unibo.firesim.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SimModelTest extends AnyFlatSpec with Matchers {

  "SimModel" should "generate the map correctly" in {
    val model = SimModel()
    val matrix = model.generateMap(500, 100)
    matrix.rows should be(500)
    matrix.cols should be(100)
  }
}