package it.unibo.firesim.model.firefighters

object MoveStrategy:

  def bresenham(from: (Int, Int), to: (Int, Int)): LazyList[(Int, Int)] =
    val deltaX = math.abs(to._1 - from._1)
    val deltaY = -math.abs(to._2 - from._2)
    val stepX = if from._1 < to._1 then 1 else -1
    val stepY = if from._2 < to._2 then 1 else -1
    val err = math.abs(to._1 - from._1) - math.abs(to._2 - from._2)

    def next(x: Int, y: Int, err: Int): (Int, Int, Int) =
      if (x, y) == to then
        (x, y, err)
      else
        var (nx, ny) = (x, y)
        val e2 = 2 * err
        var newErr = err
        if e2 >= deltaY then
          newErr += deltaY
          nx += stepX
        if e2 <= deltaX then
          newErr += deltaX
          ny += stepY
        (nx, ny, newErr)

    LazyList.iterate(next(from._1, from._2, err))(next).map(e => (e._1, e._2))
