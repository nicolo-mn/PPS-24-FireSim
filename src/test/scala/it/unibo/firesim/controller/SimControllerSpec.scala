package it.unibo.firesim.controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.*
import it.unibo.firesim.model.SimModel

class SimControllerSpec extends AnyFlatSpec with Matchers:

  "SimController" should "set wind speed in model when SetWindSpeed message is handled" in {
    val model = new SimModel()
    val controller = new SimController(model)

    controller.handleViewMessage(SetWindSpeed(7.5))

//    model.getSimParams.windSpeed shouldBe 7.5
  }
