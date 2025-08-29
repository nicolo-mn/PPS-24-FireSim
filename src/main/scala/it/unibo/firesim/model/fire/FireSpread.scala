package it.unibo.firesim.model.fire

import it.unibo.firesim.model.{Matrix, SimParams, inBounds, update}
import it.unibo.firesim.model.cell.CellType
import it.unibo.firesim.model.fire.* // Assuming your fire logic is here

/** A helper function to get valid neighbor positions. It uses the `inBounds`
  * extension method from your Matrix type.
  */
def fireSpread(
    matrix: Matrix,
    burning: Set[(Int, Int)],
    params: SimParams,
    currentCycle: Int,
    randoms: LazyList[Double]
)(using
    prob: ProbabilityCalc,
    burn: BurnDurationPolicy
): (Matrix, Set[(Int, Int)], LazyList[Double]) =

  val (stillBurningUpdates, extinguishedPositions) = burning.foldLeft(
    (Map.empty[(Int, Int), CellType], Set.empty[(Int, Int)])
  ) { case ((burningAcc, extinguishedAcc), pos) =>
    matrix(pos._1)(pos._2) match

      case CellType.Burning(start, fireStage, oldCellType) =>

        if burn(oldCellType, start, currentCycle) then
          (burningAcc, extinguishedAcc + pos)
        else
          val nextStage = FireStage.nextStage(
            start,
            currentCycle,
            oldCellType.vegetation.burnDuration
          )
          (
            burningAcc + (pos -> CellType.Burning(
              start,
              nextStage,
              oldCellType
            )),
            extinguishedAcc
          )
      case _ =>
        (burningAcc, extinguishedAcc)
  }

  val stillBurningPos = stillBurningUpdates.keys.toSet

  val ignitionCandidates = burning
    .flatMap(pos => neighbors(pos._1, pos._2, matrix))
    .filter { pos =>
      val cell = matrix(pos._1)(pos._2)
      cell.isFlammable && !cell.isBurning
    }

  val (newlyIgnited, remainingRandoms) = ignitionCandidates.foldLeft(
    (Map.empty[(Int, Int), CellType], randoms)
  ) { case ((ignitedAcc, rand), pos) =>
    val cell = matrix(pos._1)(pos._2)
    val ignitionProb = prob(cell, params, pos._1, pos._2, matrix)

    if rand.head < ignitionProb then
      val newBurningCell = CellType.Burning(
        currentCycle,
        FireStage.Ignition,
        matrix(pos._1)(pos._2)
      )
      (ignitedAcc + (pos -> newBurningCell), rand.tail)
    else
      (ignitedAcc, rand.tail)
  }

  val allChanges = stillBurningUpdates ++
    newlyIgnited ++
    extinguishedPositions.map(p => (p, CellType.Burnt))

  val newMatrix = allChanges.foldLeft(matrix)((m, change) =>
    m.update(change._1._1, change._1._2, change._2)
  )

  val newBurningSet = stillBurningPos ++ newlyIgnited.keys
  (newMatrix, newBurningSet, remainingRandoms)

private def neighbors(r: Int, c: Int, matrix: Matrix): Seq[(Int, Int)] =
  for
    dr <- -1 to 1
    dc <- -1 to 1
    if !(dr == 0 && dc == 0) && matrix.inBounds(r + dr, c + dc)
  yield (r + dr, c + dc)
