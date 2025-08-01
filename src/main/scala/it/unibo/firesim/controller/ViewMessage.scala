package it.unibo.firesim.controller

/**
 * Base trait for all messages that can be sent from the view to the controller.
 * Each message knows how to execute itself by invoking the appropriate method on the controller.
 */
sealed trait ViewMessage:
  /**
   * Executes the corresponding action on the given controller.
   * @param controller Controller of Simulation
   */
  def execute(controller: SimController): Unit

/**
 * Message to update the wind speed parameter.
 * @param value The new wind speed value.
 */
case class SetWindSpeed(value: Double) extends ViewMessage:
  def execute(controller: SimController): Unit = controller.setWindSpeed(value)

/**
 * Message to update the wind angle parameter.
 * @param value The new wind angle value.
 */
case class SetWindAngle(value: Double) extends ViewMessage:
  def execute(controller: SimController): Unit = controller.setWindAngle(value)

/**
 * Message to update the temperature parameter.
 * @param value The new temperature value.The new temperature value.
 */
case class SetTemperature(value: Double) extends ViewMessage:
  def execute(controller: SimController): Unit = controller.setTemperature(value)

/**
 * Message to update the humidity parameter.
 * @param value The new humidity value.
 */
case class SetHumidity(value: Double) extends ViewMessage:
  def execute(controller: SimController): Unit = controller.setHumidity(value)

/**
 * Message to generate a new simulation map with the given dimensions.
 * @param width The width of the map.
 * @param height The height of the map.
 */
case class GenerateMap(width: Int, height: Int) extends ViewMessage:
  def execute(controller: SimController): Unit =
    controller.generateMap(width, height)

/**
 * Message to start the simulation.
 */
case object StartSimulation extends ViewMessage:
  def execute(controller: SimController): Unit =
    controller.startSimulation()

/**
 * Message to pause or resume the simulation.
 */
case object PauseResumeSimulation extends ViewMessage:
  def execute(controller: SimController): Unit =
    controller.pauseResumeSimulation()

/**
 * Message to stop the simulation.
 */
case object StopSimulation extends ViewMessage:
  def execute(controller: SimController): Unit = controller.stopSimulation()

/**
 * Message to place a fire at the specified position on the map.
 * @param pos The (x, y) coordinates where the fire should be placed.
 */
case class PlaceFire(pos: (Int, Int)) extends ViewMessage:
  def execute(controller: SimController): Unit = controller.placeFire(pos)

/**
 * Message to place a barrier on the map between two positions.
 * @param startPos The (x, y) starting coordinates of the barrier.
 * @param finishPos The (x, y) finishing coordinates of the barrier.
 */
case class PlaceBarrier(startPos: (Int, Int), finishPos: (Int, Int))
    extends ViewMessage:
  def execute(controller: SimController): Unit =
    controller.placeBarrier(startPos, finishPos)
