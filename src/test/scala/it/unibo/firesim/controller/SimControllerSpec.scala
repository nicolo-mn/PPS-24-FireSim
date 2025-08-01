package it.unibo.firesim.controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.firesim.*
import it.unibo.firesim.debug.MockSimModel
class SimControllerSpec extends AnyFlatSpec with Matchers {

  "SimController" should "set wind speed in model when SetWindSpeed message is handled" in {
    val model = new MockSimModel
    val updater = new SimUpdater()
    val controller = new SimController(model, updater)

    controller.handleViewMessage(SetWindSpeed(7.5))

    model.getSimParams.windSpeed shouldBe 7.5
  }


}