package it.unibo.firesim.controller

import it.unibo.firesim.model.{CellType, Matrix, SimModel, update}
import it.unibo.firesim.util.{Line, Logger}
import it.unibo.firesim.util.Line.*
import it.unibo.firesim.view.SimView

import scala.jdk.CollectionConverters.*
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

  private var matrix: Matrix = Vector.empty
  private var originalTickMs: Int = 0
  private var currentGeneration: Int = 0

  @volatile private var running: Boolean = false
  @volatile private var mapGenerated: Boolean = false
  @volatile private var isClosing: Boolean = false
  @volatile private var width, height: Int = 0
  @volatile private var tickMs: Int = 0

  private val simView = new SimView(this)
  private val placeQueue = new LinkedBlockingQueue[((Int, Int), CellType)]()

  /** Handles a message from the view by executing the corresponding action on
    * this controller.
    *
    * @param msg
    *   The message to process.
    */
  override def handleViewMessage(msg: ViewMessage): Unit = msg.execute(this)

  /** Change the milliseconds to wait every tick using the speed factor
    *
    * @param factor
    *   The speed factor used to divide the original tick milliseconds
    */
  private[controller] def updateSimulationSpeed(factor: Double): Unit =
    tickMs = (originalTickMs / factor).toInt

  /** Sets the wind speed from view to model.
    *
    * @param speed
    *   The wind speed to update.
    */
  private[controller] def setWindSpeed(speed: Int): Unit =
    model.updateParams(_.copy(windSpeed = speed))

  /** Sets the wind angle from view to model.
    *
    * @param angle
    *   The angle to update.
    */
  private[controller] def setWindAngle(angle: Int): Unit =
    model.updateParams(_.copy(windAngle = angle))

  /** Sets the temperature from view to model.
    *
    * @param temp
    *   The temperature to update.
    */
  private[controller] def setTemperature(temp: Int): Unit =
    model.updateParams(_.copy(temperature = temp))

  /** Sets the humidity from view to model.
    *
    * @param humidity
    *   The humidity to update.
    */
  private[controller] def setHumidity(humidity: Int): Unit =
    model.updateParams(_.copy(humidity = humidity))

  /** Makes model generate a map.
    *
    * @param width
    *   The width of the map.
    * @param height
    *   The height of the map.
    */
  private[controller] def generateMap(width: Int, height: Int): Unit =
    lock.synchronized {
      this.width = width
      this.height = height
      lock.notifyAll()
    }

  /** Makes model try to place a cell.
    *
    * @param pos
    *   The position of the cell.
    * @param cellViewType
    *   The type of cell.
    */
  private[controller] def placeCell(
      pos: (Int, Int),
      cellViewType: CellViewType
  ): Unit =
    placeQueue.put(
      pos,
      CellTypeConverter.toModel(
        cellViewType,
        matrix(pos._1)(pos._2),
        currentGeneration
      )
    )

  /** Makes model try to place a line of cells
    *
    * @param start
    *   Position start of the line
    * @param end
    *   Position end of the line
    * @param cellViewType
    *   type of line to place
    */
  private[controller] def placeLine(
      start: (Int, Int),
      end: (Int, Int),
      cellViewType: CellViewType
  ): Unit =
    Line.lineBetween(
      start,
      end
    ).withType(cellViewType).foreach(placeCell)

  /** Notifies controller that the simulation has been started.
    */
  private[controller] def startSimulation(): Unit = lock.synchronized {
    if mapGenerated then
      running = true
      lock.notifyAll()
  }

  /** Notifies controller that the simulation has been paused.
    */
  private[controller] def pauseResumeSimulation(): Unit = running = !running

  /** Notifies controller that the simulation has been stopped.
    */
  private[controller] def stopSimulation(): Unit = lock.synchronized {
    running = false
    mapGenerated = false
    width = 0
    height = 0
    placeQueue.clear()
    lock.notifyAll()
  }

  /** Notifies controller that the program is closing.
    */
  private[controller] def closing(): Unit = lock.synchronized {
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
  override def loop(tickMs: Int = 100): Unit =
    this.tickMs = tickMs
    this.originalTickMs = tickMs

    while !isClosing do
      lock.synchronized {
        while !mapGenerated do
          if width > 0 && height > 0 then
            simView.setViewMap(model.generateMap(height, width)
              .flatten.map(CellTypeConverter.toView))
            mapGenerated = true
          else lock.wait()
      }

      while mapGenerated do
        val t0 = System.currentTimeMillis()

        if running then onTick()

        handleQueuedCells()

        simView.setViewMap(matrix.flatten.map(cT =>
          CellTypeConverter.toView(cT)
        ))

        val elapsed = System.currentTimeMillis() - t0
        val remaining = this.tickMs - elapsed
        Thread.sleep(math.max(0, remaining))

  private def onTick(): Unit =
    currentGeneration = model.getCurrentCycle
    model.updateState()

  private def handleQueuedCells(): Unit =
    val buffer = new java.util.ArrayList[((Int, Int), CellType)]
    placeQueue.drainTo(buffer)
    val (newMatrix, newFirefighters) = model.placeCells(buffer.asScala.toSeq)
    matrix = newMatrix
    newFirefighters.foreach((i, j) =>
      matrix = matrix.update(i, j, CellType.Firefighter)
    )
