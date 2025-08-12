package it.unibo.firesim.model.fire

import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.CellType

def fireSpread(matrix: Matrix, params: SimParams, currentCycle: Int)(using
    prob: ProbabilityCalc,
    burn: BurnDurationPolicy,
    rand: RandomProvider
): Matrix =
  matrix.zipWithIndex.map { (row, r) =>
    row.zipWithIndex.map { (cellType, c) =>
      cellType match
        case Burning(start) =>
          if currentCycle.hasBurnedEnough(start) then
            CellType.Burnt
          else cellType

        case cellType if cellType.isFlammable =>
          if matrix.hasBurningNeighbor(
              r,
              c
            ) && rand() < prob(cellType, params, r, c, matrix)
          then CellType.Burning(currentCycle)
          else cellType

        case _ => cellType
    }
  }

def computeChangedCells(oldM: Matrix, newM: Matrix): Seq[CellType] =
  for
    (oldRow, newRow) <- oldM.zip(newM)
    (oldCell, newCell) <- oldRow.zip(newRow)
    if oldCell != newCell
  yield newCell
