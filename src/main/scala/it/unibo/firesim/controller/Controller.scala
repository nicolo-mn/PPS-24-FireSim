package it.unibo.firesim.controller

trait Controller:

  /** Handles a message from the view by executing the corresponding action on
    * this controller.
    *
    * @param msg
    *   The message to process.
    */
  def handleViewMessage(msg: ViewMessage): Unit

  /** Change the milliseconds to wait every tick using the speed factor
    *
    * @param factor
    *   The speed factor used to divide the original tick milliseconds
    */
  def updateSimulationSpeed(factor: Double): Unit

  /** Sets the wind speed from view to model.
    *
    * @param speed
    *   The wind speed to update.
    */
  def setWindSpeed(speed: Int): Unit

  /** Sets the wind angle from view to model.
    *
    * @param angle
    *   The angle to update.
    */
  def setWindAngle(angle: Int): Unit

  /** Sets the temperature from view to model.
    *
    * @param temp
    *   The temperature to update.
    */
  def setTemperature(temp: Int): Unit

  /** Sets the humidity from view to model.
    *
    * @param humidity
    *   The humidity to update.
    */
  def setHumidity(humidity: Int): Unit

  /** Makes model generate a map.
    *
    * @param width
    *   The width of the map.
    * @param height
    *   The height of the map.
    */
  def generateMap(width: Int, height: Int): Unit

  /** Makes model try to place a cell.
    *
    * @param pos
    *   The position of the cell.
    * @param cellViewType
    *   The type of cell.
    */
  def placeCell(pos: (Int, Int), cellViewType: CellViewType): Unit

  /** Notifies controller that the simulation has been started.
    */
  def startSimulation(): Unit

  /** Notifies controller that the simulation has been paused.
    */
  def pauseResumeSimulation(): Unit

  /** Notifies controller that the simulation has been stopped.
    */
  def stopSimulation(): Unit

  /** Notifies controller that the program is closing.
    */
  def closing(): Unit

  /** Makes model try to create a line.
    *
    * @param start
    *   The start of the line.
    * @param end
    *   The end of the line.
    * @param cellViewType
    *   The type of cell.
    */
  def placeLine(
      start: (Int, Int),
      end: (Int, Int),
      cellViewType: CellViewType
  ): Unit

  /** Main game loop. Once is called the main thread will stay in this loop
    * until the program is closed.
    *
    * @param tickMs
    *   The milliseconds every tick should last: could be more (simulation has
    *   delays) but not less.
    */
  def loop(tickMs: Int): Unit
