package it.unibo.firesim.model.firefighters

trait MoveStrategy:
  def move(): ((Int, Int), MoveStrategy)
  def init(from: (Int, Int), to: (Int, Int)): MoveStrategy

case class BresenhamMovement(
    position: (Int, Int),
    target: (Int, Int),
    deltaX: Int,
    deltaY: Int,
    stepX: Int,
    stepY: Int,
    err: Int
) extends MoveStrategy:

  override def move(): ((Int, Int), MoveStrategy) =
    if position == target then
      (position, this)
    else
      var (nx, ny) = position
      val e2 = 2 * err
      var newErr = err
      if e2 >= deltaY then
        newErr += deltaY
        nx += stepX
      if e2 <= deltaX then
        newErr += deltaX
        ny += stepY
      ((nx, ny), copy(position = (nx, ny), err = newErr))

  override def init(from: (Int, Int), to: (Int, Int)): MoveStrategy =
    BresenhamMovement(
      position = from,
      target = to,
      deltaX = math.abs(to._1 - from._1),
      deltaY = -math.abs(to._2 - from._2),
      stepX = if from._1 < to._1 then 1 else -1,
      stepY = if from._2 < to._2 then 1 else -1,
      err =
        math.abs(to._1 - from._1) - math.abs(to._2 - from._2)
    )
