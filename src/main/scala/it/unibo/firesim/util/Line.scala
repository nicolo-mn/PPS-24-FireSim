package it.unibo.firesim.util

import scala.annotation.tailrec

object Line:

  /**
   * Create a line from the starting point to the ending point
   *
   * @param startPos starting line position
   * @param endPos ending line position
   * @return points belonging to the line
   */
  def lineBetween(startPos: (Int, Int), endPos: (Int, Int)): Seq[(Int, Int)] =
    val (y0, x0) = startPos
    val (y1, x1) = endPos
    val deltaX = math.abs(x1 - x0)
    val deltaY = -math.abs(y1 - y0)
    val stepX = if x0 < x1 then 1 else -1
    val stepY = if y0 < y1 then 1 else -1

    @tailrec
    def loop(x: Int, y: Int, err: Int, acc: List[(Int, Int)]): List[(Int, Int)] =
      val acc1 = (y, x) :: acc
      if x == x1 && y == y1 then acc1.reverse
      else
        val e2   = 2 * err
        var nx   = x
        var ny   = y
        var err1 = err
        if e2 >= deltaY then
          err1 += deltaY
          nx += stepX
        if e2 <= deltaX then
          err1 += deltaX
          ny += stepY

        val extras =
          if nx != x && ny != y then List((y, nx), (ny, x)) else Nil

        val nextAcc = (y, x) :: extras ::: acc
        loop(nx, ny, err1, nextAcc)

    loop(x0, y0, deltaX + deltaY, Nil)

  extension [T](points: Seq[(Int, Int)])
    /**
     * Pairs each point with a constant value `t`, producing ((row, col), t) tuples.
     * Useful to convert a polyline into placement commands carrying a fixed payload
     *
     * @param t value to associate with each point
     * @return sequence of ((row, col), t) pairs, same length as 'points'
     */
    def withType(t: T): Seq[((Int, Int), T)] = points.map(_ -> t)

