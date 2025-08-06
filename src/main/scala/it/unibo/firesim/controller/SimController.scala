package it.unibo.firesim.controller

import it.unibo.firesim.model.{SimModel, SimParams}
import it.unibo.firesim.view.SimView

/** SimController coordinates the interactions between the simulation model, the
  * updater (tick scheduler), and the view. It safely updates the model
  * parameters and processes user commands via messages.
  *
  * @param model
  *   The simulation model.
  */
class SimController(
    model: SimModel
) extends Controller:

  private val simView = new SimView(this)

  private var windSpeed: Double = model.getSimParams.windSpeed
  private var windAngle: Double = model.getSimParams.windAngle
  private var temperature: Double = model.getSimParams.temperature
  private var humidity: Double = model.getSimParams.humidity
  private var running: Boolean = false
  private var paused: Boolean = false


//  private val mutex = new Object
  //  /** Utility function to update the model with thread safety.
  //    * @param setter
  //    *   The model setter function.
  //    * @param value
  //    *   The value to set.
  //    * @tparam T
  //    */
  //  private def updateModel[T](setter: T => Unit, value: T): Unit =
  //    mutex.synchronized { setter(value) }

  /** Handles a message from the view by executing the corresponding action on
    * this controller.
    * @param msg
    *   The message to process.
    */
  override def handleViewMessage(msg: ViewMessage): Unit = msg.execute(this)

  override def setWindSpeed(speed: Double): Unit = this.windSpeed = speed

  override def setWindAngle(angle: Double): Unit = this.windAngle = angle

  override def setTemperature(temp: Double): Unit = this.temperature = temp

  override def setHumidity(humidity: Double): Unit = this.humidity = humidity

  override def generateMap(width: Int, height: Int): Unit = ???

  override def placeCell(pos: (Int, Int), cellViewType: CellViewType): Unit = ???

  override def startSimulation(): Unit = ???

  override def pauseResumeSimulation(): Unit = ???

  override def stopSimulation(): Unit = ???

  /** Logic to be executed at each simulation tick.
    */
  private def onTick(): Unit = ???

