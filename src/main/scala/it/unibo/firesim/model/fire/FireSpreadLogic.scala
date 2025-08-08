package it.unibo.firesim.model.fire

import it.unibo.firesim.model.{Matrix, SimParams, inBounds}
import it.unibo.firesim.model.cell.{Cell, CellType}

type ProbabilityCalc = (Cell, SimParams, Int, Int, Matrix) => Double
type BurnDurationPolicy = (Int, Int) => Boolean
type RandomProvider = () => Double

extension (cellType: CellType)

  def isFlammable: Boolean = cellType match
    case CellType.Forest | CellType.Grass => true
    case _                                => false

extension (matrix: Matrix)

  def burningNeighbors(r: Int, c: Int): Seq[Cell] =
    for
      dr <- -1 to 1
      dc <- -1 to 1
      if !(dr == 0 && dc == 0)
      nr = r + dr
      nc = c + dc
      if matrix.inBounds(nr, nc)
      cell = matrix(nr)(nc)
      if cell.cellType.isInstanceOf[CellType.Burning]
    yield cell

extension (cycle: Int)

  def hasBurnedEnough(start: Int)(using policy: BurnDurationPolicy): Boolean =
    policy(start, cycle)

object Burning:

  def unapply(cellType: CellType): Option[Int] = cellType match
    case CellType.Burning(start) => Some(start)
    case _                       => None
