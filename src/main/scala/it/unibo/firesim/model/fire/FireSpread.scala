package it.unibo.firesim.model.fire

import it.unibo.firesim.model.{Matrix, SimParams, inBounds}
import it.unibo.firesim.model.cell.CellType
import it.unibo.firesim.model.fire.{BurnDurationPolicy, ProbabilityCalc}

def fireSpread(
    matrix: Matrix,
    burningCells: Set[(Int, Int)],
    params: SimParams,
    currentCycle: Int,
    randoms: LazyList[Double]
)(using
    prob: ProbabilityCalc,
    burn: BurnDurationPolicy
): (Matrix, Set[(Int, Int)], LazyList[Double]) =

  if burningCells.isEmpty then
    (matrix, burningCells, randoms)
  else {
    val rows = matrix.length
    val cols = if rows > 0 then matrix(0).length else 0
    val newMatrix = Array.ofDim[CellType](rows, cols)

    var newBurningCells = Set.empty[(Int, Int)]
    var rng = randoms

    for r <- 0 until rows; c <- 0 until cols do
      val currentCell = matrix(r)(c)

      currentCell match
        case CellType.Burning(start, _, originalType) =>
          if burn(originalType, start, currentCycle) then
            newMatrix(r)(c) = CellType.Burnt
          else
            val updatedStage = FireStage.nextStage(
              start,
              currentCycle,
              Vegetation.burnDuration(CellTypeOps.vegetation(originalType))
            )
            val updated = CellType.Burning(start, updatedStage, originalType)
            newMatrix(r)(c) = updated
            newBurningCells += ((r, c))

        case _ =>
          val burningNeighbors = neighbors(r, c, matrix).filter(p =>
            CellTypeOps.isBurning(matrix(p._1)(p._2))
          )
          if CellTypeOps.isFlammable(currentCell) && burningNeighbors.nonEmpty
          then
            val randVal = rng.head
            rng = rng.tail
            if randVal < prob(currentCell, params, r, c, matrix) then
              newMatrix(r)(c) =
                CellType.Burning(currentCycle, FireStage.Ignition, currentCell)
              newBurningCells += ((r, c))
            else
              newMatrix(r)(c) = currentCell
          else
            newMatrix(r)(c) = currentCell

    (newMatrix.map(_.toVector).toVector, newBurningCells, rng)
    }

def neighbors(r: Int, c: Int, matrix: Matrix): Seq[(Int, Int)] =
  for
    dr <- -1 to 1
    dc <- -1 to 1
    if !(dr == 0 && dc == 0) && matrix.inBounds(r + dr, c + dc)
  yield (r + dr, c + dc)
