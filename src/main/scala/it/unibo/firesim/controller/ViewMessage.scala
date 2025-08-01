package it.unibo.firesim.controller

sealed trait ViewMessage {
  def execute(controller: SimController): Unit
}

case class SetWindSpeed(value: Double) extends ViewMessage {
  def execute(controller: SimController): Unit = controller.setWindSpeed(value)
}

case class SetWindAngle(value: Double) extends ViewMessage {
  def execute(controller: SimController): Unit = controller.setWindAngle(value)
}

case class SetTemperature(value: Double) extends ViewMessage {
  def execute(controller: SimController): Unit = controller.setTemperature(value)
}

case class SetHumidity(value: Double) extends ViewMessage {
  def execute(controller: SimController): Unit = controller.setHumidity(value)
}

case class GenerateMap(width: Int, height: Int) extends ViewMessage {
  def execute(controller: SimController): Unit = controller.generateMap(width, height)
}

case object StartSimulation extends ViewMessage {
  def execute(controller: SimController): Unit = controller.startSimulation()
}

case object PauseResumeSimulation extends ViewMessage {
  def execute(controller: SimController): Unit = controller.pauseResumeSimulation()
}

case object StopSimulation extends ViewMessage {
  def execute(controller: SimController): Unit = controller.stopSimulation()
}

case class PlaceFire(pos: (Int, Int)) extends ViewMessage {
  def execute(controller: SimController): Unit = controller.placeFire(pos)
}

case class PlaceBarrier(startPos: (Int, Int), finishPos: (Int, Int)) extends ViewMessage {
  def execute(controller: SimController): Unit = controller.placeBarrier(startPos, finishPos)
}
