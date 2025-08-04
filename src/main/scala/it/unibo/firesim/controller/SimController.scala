package it.unibo.firesim.controller

import it.unibo.firesim.model.{SimParams, SimModel}

/** SimController coordinates the interactions between the simulation model, the
  * updater (tick scheduler), and the view. It safely updates the model
  * parameters and processes user commands via messages.
  *
  * @param model
  *   The simulation model.
  * @param updater
  *   The simulation updater handling the ticks.
  */
class SimController(
    model: SimModel,
    updater: SimUpdater
) extends Controller:

  private val mutex = new Object

  /** Handles a message from the view by executing the corresponding action on
    * this controller.
    * @param msg
    *   The message to process.
    */
  def handleViewMessage(msg: ViewMessage): Unit = msg.execute(this)

  /** Utility function to update the model with thread safety.
    * @param setter
    *   The model setter function.
    * @param value
    *   The value to set.
    * @tparam T
    */
  private def updateModel[T](setter: T => Unit, value: T): Unit =
    mutex.synchronized { setter(value) }

  def setWindSpeed(speed: Double): Unit = updateModel(model.setWindSpeed, speed)
  def setWindAngle(angle: Double): Unit = updateModel(model.setWindAngle, angle)

  def setTemperature(temp: Double): Unit =
    updateModel(model.setTemperature, temp)

  def setHumidity(humidity: Double): Unit =
    updateModel(model.setHumidity, humidity)

  def getSimParams: SimParams = mutex.synchronized {
    model.getSimParams
  }

  def generateMap(width: Int, height: Int): Unit = ???
  def placeFire(pos: (Int, Int)): Unit = ???
  def placeBarrier(startPos: (Int, Int), finishPos: (Int, Int)): Unit = ???

  def startSimulation(): Unit = updater.start()
  def pauseResumeSimulation(): Unit = updater.pauseResume()
  def stopSimulation(): Unit = updater.stop()

  /** Sets the view and the callback to be called by the updater on each
    * simulation tick.
    */
  def setView(): Unit =
    updater.setUpdateCallback(() => onTick())

  /** Logic to be executed at each simulation tick.
    */
  private def onTick(): Unit = mutex.synchronized {
    try {} catch
      case ex: Exception =>
        ex.printStackTrace()
  }
