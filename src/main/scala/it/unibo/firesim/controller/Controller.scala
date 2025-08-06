package it.unibo.firesim.controller

import it.unibo.firesim.model.SimParams

trait Controller:
  def handleViewMessage(msg: ViewMessage): Unit
  def setWindSpeed(speed: Double): Unit
  def setWindAngle(angle: Double): Unit
  def setTemperature(temp: Double): Unit
  def setHumidity(humidity: Double): Unit
  def generateMap(width: Int, height: Int): Unit
  def placeCell(pos: (Int, Int), cellViewType: CellViewType): Unit
  def startSimulation(): Unit
  def pauseResumeSimulation(): Unit
  def stopSimulation(): Unit
