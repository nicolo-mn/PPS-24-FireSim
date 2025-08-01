package it.unibo.firesim.controller

import it.unibo.firesim.debug.MockSimModel
import it.unibo.firesim.debug.SimParams

class SimController(
                     model: MockSimModel,
                     updater: SimUpdater
                   ) {

  private val mutex = new Object

  def handleViewMessage(msg: ViewMessage): Unit = msg.execute(this)

  private def updateModel[T](setter: T => Unit, value: T): Unit =
    mutex.synchronized { setter(value) }

  def setWindSpeed(speed: Double): Unit = updateModel(model.setWindSpeed, speed)
  def setWindAngle(angle: Double): Unit = updateModel(model.setWindAngle, angle)
  def setTemperature(temp: Double): Unit = updateModel(model.setTemperature, temp)
  def setHumidity(humidity: Double): Unit = updateModel(model.setHumidity, humidity)

  def generateMap(width: Int, height: Int): Unit = ???
  def placeFire(pos: (Int, Int)): Unit = ???
  def placeBarrier(startPos: (Int, Int), finishPos: (Int, Int)): Unit = ???

  def startSimulation(): Unit = ???
  def pauseResumeSimulation(): Unit = ???
  def stopSimulation(): Unit = ???

  def getSimParams: SimParams = mutex.synchronized { model.getSimParams }

}

