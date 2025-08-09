package it.unibo.firesim.controller

import it.unibo.firesim.model.cell.CellType
import it.unibo.firesim.model.{SimModel, SimParams}
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
  @volatile private var paused: Boolean = false
  @volatile private var mapGenerated: Boolean = false
  @volatile private var isClosing: Boolean = false
  @volatile private var width, height: Int = 0

  private val simView = new SimView(this)
  private val placeQueue = new LinkedBlockingQueue[((Int, Int), CellType)]()

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

  override def generateMap(width: Int, height: Int): Unit = lock.synchronized {
    this.width = width
    this.height = height
    lock.notifyAll()
  }

  override def placeCell(pos: (Int, Int), cellViewType: CellViewType): Unit =
    placeQueue.put((pos, CellTypeConverter.toModel(cellViewType)))

  override def startSimulation(): Unit = lock.synchronized {
    if mapGenerated then
      running = true
      paused = false
      lock.notifyAll()
  }

  override def pauseResumeSimulation(): Unit = paused = !paused

  override def stopSimulation(): Unit = lock.synchronized {
    running = false
    paused = false
    mapGenerated = false
    placeQueue.clear()
    lock.notifyAll()
  }

  override def closing(): Unit = isClosing = true

  override def loop(tickMs: Long = 100): Unit =
    while !isClosing do
      lock.synchronized {
        while !mapGenerated || !running do
          lock.wait()
          if width > 0 && height > 0 then
            simView.setViewMap(model.generateMap(height, width).cells
              .flatten.map(c => CellTypeConverter.toView(c.cellType)))
            mapGenerated = true
      }

      while running && mapGenerated do
        val t0 = System.currentTimeMillis()

        if !paused then
          onTick()
          handleQueuedCells()

        val elapsed = System.currentTimeMillis() - t0
        val remaining = tickMs - elapsed
        Thread.sleep(math.max(0, remaining))

  private def onTick(): Unit = ???

  private def handleQueuedCells(): Unit =
    placeQueue.forEach((pos, cellType) => model.placeCell(pos, cellType))
    placeQueue.clear()


