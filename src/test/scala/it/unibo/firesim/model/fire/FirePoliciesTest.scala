package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.grassBurnDuration
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.model.SimParams
import it.unibo.firesim.model.fire.FireStage.Ignition
import it.unibo.firesim.model.map.CellType
import it.unibo.firesim.model.map.CellType.Grass

class FirePoliciesTest extends AnyFlatSpec with Matchers:

  it should "give higher probability for grass than for forest" in {
    val dummyMatrix = Vector.empty[Vector[CellType]]
    val params = SimParams(0, 0, 30, 20)

    val forestProb =
      defaultProbabilityCalc(CellType.Forest, params, (0, 0), dummyMatrix)
    val grassProb =
      defaultProbabilityCalc(CellType.Grass, params, (0, 1), dummyMatrix)

    grassProb should be > forestProb
    forestProb should be <= 1.0
    grassProb should be >= 0.0
  }

  it should "increase probability with higher temperature" in {
    val matrix = Vector(Vector(CellType.Burning(0, Ignition, Grass)))
    val paramsLowTemp = SimParams(0, 0, 10, 50) // Temp: 10
    val paramsHighTemp = SimParams(0, 0, 40, 50) // Temp: 40

    val probLowTemp =
      defaultProbabilityCalc(Grass, paramsLowTemp, (0, 1), matrix)
    val probHighTemp =
      defaultProbabilityCalc(Grass, paramsHighTemp, (0, 1), matrix)

    probHighTemp should be > probLowTemp
  }

  it should "turn burning cells into burnt after after their burn duration" in {
    val testBurnDuration = defaultBurnDuration(Grass, 0, 10)
    testBurnDuration shouldBe false

    val testEndBurnDuration = defaultBurnDuration(Grass, 0, grassBurnDuration)
    testEndBurnDuration shouldBe true
  }
