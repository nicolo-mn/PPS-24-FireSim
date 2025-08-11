package it.unibo.firesim.model.fire

import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.{Cell, CellType}

def fireSpread(matrix: Matrix, params: SimParams, currentCycle: Int)(using
    prob: ProbabilityCalc,
    burn: BurnDurationPolicy,
    rand: RandomProvider
): (Matrix, Seq[Cell]) =
  val newMatrix = matrix.zipWithIndex.map { (row, r) =>
    row.zipWithIndex.map { (cell, c) =>
      cell.cellType match
        case Burning(start) =>
          if currentCycle.hasBurnedEnough(start) then
            cell.copy(cellType = CellType.Burnt)
          else cell

        case cellType if cellType.isFlammable =>
          if matrix.hasBurningNeighbor(
              r,
              c
            ) && rand() < prob(cell, params, r, c, matrix)
          then cell.copy(cellType = CellType.Burning(currentCycle))
          else cell

        case _ => cell
    }
  }

  (newMatrix, computeChangedCells(matrix, newMatrix))

def computeChangedCells(oldM: Matrix, newM: Matrix): Seq[Cell] =
  for
    (oldRow, newRow) <- oldM.zip(newM)
    (oldCell, newCell) <- oldRow.zip(newRow)
    if oldCell.cellType != newCell.cellType
  yield newCell
