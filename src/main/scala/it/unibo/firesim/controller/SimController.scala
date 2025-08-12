package it.unibo.firesim.controller

import it.unibo.firesim.model.cell.CellType
import it.unibo.firesim.model.{SimModel, SimParams}
import it.unibo.firesim.util.Logger
import it.unibo.firesim.view.SimView

import java.util.concurrent.LinkedBlockingQueue

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

  private val lock = Object()

  private var windSpeed: Double = model.getSimParams.windSpeed
  private var windAngle: Double = model.getSimParams.windAngle
  private var temperature: Double = model.getSimParams.temperature
  private var humidity: Double = model.getSimParams.humidity

  @volatile private var running: Boolean = false
  @volatile private var mapGenerated: Boolean = false
  @volatile private var isClosing: Boolean = false
  @volatile private var width, height: Int = 0

  private val simView = new SimView(this)
  private val placeQueue = new LinkedBlockingQueue[((Int, Int), CellType)]()

  /** Handles a message from the view by executing the corresponding action on
    * this controller.
    *
    * @param msg
    *   The message to process.
    */
  override def handleViewMessage(msg: ViewMessage): Unit = msg.execute(this)

  /** Asynchronously sets the wind speed from view to model.
    *
    * @param speed
    *   The wind speed to update.
    */
  override def setWindSpeed(speed: Double): Unit = this.windSpeed = speed

  /** Asynchronously sets the wind angle from view to model.
    *
    * @param angle
    *   The angle to update.
    */
  override def setWindAngle(angle: Double): Unit = this.windAngle = angle

  /** Asynchronously sets the temperature from view to model.
    *
    * @param temp
    *   The temperature to update.
    */
  override def setTemperature(temp: Double): Unit = this.temperature = temp

  /** Asynchronously sets the humidity from view to model.
    *
    * @param humidity
    *   The humidity to update.
    */
  override def setHumidity(humidity: Double): Unit = this.humidity = humidity

  /** Asynchronously makes model generate a map.
    *
    * @param width
    *   The width of the map.
    * @param height
    *   The height of the map.
    */
  override def generateMap(width: Int, height: Int): Unit = lock.synchronized {
    this.width = width
    this.height = height
    lock.notifyAll()
  }

  /** Asynchronously makes model try to place a cell.
    *
    * @param pos
    *   The position of the cell.
    * @param cellViewType
    *   The type of cell.
    */
  override def placeCell(pos: (Int, Int), cellViewType: CellViewType): Unit =
    placeQueue.put((pos, CellTypeConverter.toModel(cellViewType)))

  /** Notifies controller that the simulation has been started.
    */
  override def startSimulation(): Unit = lock.synchronized {
    if mapGenerated then
      running = true
      lock.notifyAll()
  }

  /** Notifies controller that the simulation has been paused.
    */
  override def pauseResumeSimulation(): Unit = running = !running

  /** Notifies controller that the simulation has been stopped.
    */
  override def stopSimulation(): Unit = lock.synchronized {
    running = false
    mapGenerated = false
    width = 0
    height = 0
    placeQueue.clear()
    lock.notifyAll()
  }

  /** Notifies controller that the program is closing.
    */
  override def closing(): Unit = lock.synchronized {
    isClosing = true
    running = false
    mapGenerated = false
    placeQueue.clear()
    lock.notifyAll()
  }

  /** Main game loop. Once is called the main thread will stay in this loop
    * until the program is closed.
    *
    * @param tickMs
    *   The milliseconds every tick should last: could be more (simulation has
    *   delays) but not less.
    */
  override def loop(tickMs: Long = 100): Unit =
    while !isClosing do
      lock.synchronized {
        while !mapGenerated do
          if width > 0 && height > 0 then
            simView.setViewMap(model.generateMap(height, width)
              .flatten.map(CellTypeConverter.toView))
            mapGenerated = true
            Logger.log(getClass, "map generated successfully")
          lock.wait()
      }

      while mapGenerated do
        val t0 = System.currentTimeMillis()

        if running then onTick()

        handleQueuedCells()

        // TODO: update view map with updated map from model

        val elapsed = System.currentTimeMillis() - t0
        val remaining = tickMs - elapsed
        Thread.sleep(math.max(0, remaining))

  private def onTick(): Unit =
    model.updateState(SimParams(windSpeed, windAngle, temperature, humidity))

  private def handleQueuedCells(): Unit =
    placeQueue.forEach((pos, cellType) => model.placeCell(pos, cellType))
    placeQueue.clear()
