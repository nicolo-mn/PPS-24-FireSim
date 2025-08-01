package it.unibo.firesim.controller

import it.unibo.firesim.debug.SimParams

trait Controller {
  def handleViewMessage(msg: ViewMessage): Unit
  def setWindSpeed(speed: Double): Unit
  def setWindAngle(angle: Double): Unit
  def setTemperature(temp: Double): Unit
  def setHumidity(humidity: Double): Unit
  def getSimParams: SimParams
  def generateMap(width: Int, height: Int): Unit
  def placeFire(pos: (Int, Int)): Unit
  def placeBarrier(startPos: (Int, Int), finishPos: (Int, Int)): Unit
  def startSimulation(): Unit
  def pauseResumeSimulation(): Unit
  def stopSimulation(): Unit
  def setView(): Unit
}
