package it.unibo.firesim.model.fire

import it.unibo.firesim.model.{Matrix, SimParams}
import it.unibo.firesim.model.cell.CellType

def fireSpread(matrix: Matrix, params: SimParams, currentCycle: Int)(using
    prob: ProbabilityCalc,
    burn: BurnDurationPolicy,
    rand: RandomProvider
): Matrix =
  matrix.zipWithIndex.map { (row, r) =>
    row.zipWithIndex.map { (cell, c) =>
      cell.cellType match
        case Burning(start) =>
          if currentCycle.hasBurnedEnough(start) then
            cell.copy(cellType = CellType.Burnt)
          else cell

        case cellType if cellType.isFlammable =>
          val hasBurningNeighbor = matrix.burningNeighbors(r, c).nonEmpty
          if hasBurningNeighbor && rand() < prob(cell, params, r, c, matrix)
          then cell.copy(cellType = CellType.Burning(currentCycle))
          else cell

        case _ => cell
    }
  }
