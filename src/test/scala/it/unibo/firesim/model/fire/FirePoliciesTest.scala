package it.unibo.firesim.model.fire

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.SimParams
import it.unibo.firesim.model.map.CellType

class FirePoliciesTest extends AnyFlatSpec with Matchers:

  it should "give higher probability for grass than for forest" in {
    val dummyMatrix = Vector.empty[Vector[CellType]]
    val params = SimParams(0, 0, 30, 20)

    val forestProb =
      defaultProbabilityCalc(CellType.Forest, params, 0, 0, dummyMatrix)
    val grassProb =
      defaultProbabilityCalc(CellType.Grass, params, 0, 1, dummyMatrix)

    grassProb should be > forestProb
    forestProb should be <= 1.0
    grassProb should be >= 0.0
  }
