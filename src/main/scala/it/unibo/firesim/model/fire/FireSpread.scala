package it.unibo.firesim.model.fire

import it.unibo.firesim.model.map.{CellType, Matrix}
import it.unibo.firesim.model.SimParams
import it.unibo.firesim.model.map.{inBounds, update}
import it.unibo.firesim.util.RNG

/** Executes one simulation step of fire spread across the grid.
  *
  * @param matrix
  *   current simulation grid.
  * @param burning
  *   set of positions (row, col) that are currently burning
  * @param params
  *   the global simulation parameters
  * @param currentCycle
  *   the current simulation cycle
  * @param rng
  *   the random number generator state
  * @param prob
  *   the ignition probability function
  * @param burn
  *   the burn duration policy
  * @return
  *   a tuple with:
  *   - the updated grid (`Matrix`)
  *   - the updated set of burning positions
  *   - the updated random number generator
  */
def fireSpread(
    matrix: Matrix,
    burning: Set[(Int, Int)],
    params: SimParams,
    currentCycle: Int,
    rng: RNG
)(using
    prob: ProbabilityCalc,
    burn: BurnDurationPolicy
): (Matrix, Set[(Int, Int)], RNG) =

  val (stillBurningUpdates, extinguishedPositions) =
    updateBurningCells(matrix, burning, burn, currentCycle)
  val stillBurningPos = stillBurningUpdates.keys.toSet

  val ignitionCandidates = burning
    .flatMap(pos => neighbors(pos._1, pos._2, matrix))
    .filter { pos =>
      val cell = matrix(pos._1)(pos._2)
      cell.isFlammable && !cell.isBurning
    }

  val (newlyIgnited, finalRng) =
    igniteNewFires(ignitionCandidates, prob, rng, params, matrix, currentCycle)

  val allChanges = stillBurningUpdates ++
    newlyIgnited ++
    extinguishedPositions.map(p => (p, CellType.Burnt))

  val newMatrix = allChanges.foldLeft(matrix)((m, change) =>
    m.update(change._1._1, change._1._2, change._2)
  )

  val newBurningSet = stillBurningPos ++ newlyIgnited.keys
  (newMatrix, newBurningSet, finalRng)

private def neighbors(r: Int, c: Int, matrix: Matrix): Seq[(Int, Int)] =
  for
    dr <- -1 to 1
    dc <- -1 to 1
    if !(dr == 0 && dc == 0) && matrix.inBounds(r + dr, c + dc)
  yield (r + dr, c + dc)

private def updateBurningCells(
    matrix: Matrix,
    burning: Set[(Int, Int)],
    burnt: BurnDurationPolicy,
    currentCycle: Int
): (Map[(Int, Int), CellType], Set[(Int, Int)]) =
  // Iterate over burning cells and decide whether they keep burning,
  // transition to the next fire stage, or extinguish
  burning.foldLeft(
    (Map.empty[(Int, Int), CellType], Set.empty[(Int, Int)])
  ) { case ((burningAcc, extinguishedAcc), pos) =>
    matrix(pos._1)(pos._2) match

      case CellType.Burning(start, fireStage, oldCellType) =>

        if burnt(oldCellType, start, currentCycle) then
          (burningAcc, extinguishedAcc + pos)
        else
          val nextStage = FireStage.nextStage(
            start,
            currentCycle,
            oldCellType.vegetation.burnDuration
          )
          if nextStage != fireStage then
            (
              burningAcc + (pos -> CellType.Burning(
                start,
                nextStage,
                oldCellType
              )),
              extinguishedAcc
            )
          else
            (burningAcc, extinguishedAcc)
      case _ =>
        (burningAcc, extinguishedAcc)
  }

private def igniteNewFires(
    ignitionCandidates: Set[(Int, Int)],
    prob: ProbabilityCalc,
    rng: RNG,
    params: SimParams,
    matrix: Matrix,
    currentCycle: Int
): (Map[(Int, Int), CellType], RNG) =
  ignitionCandidates.foldLeft(
    (Map.empty[(Int, Int), CellType], rng)
  ) { case ((ignitedAcc, r), pos) =>

    val cell = matrix(pos._1)(pos._2)
    val ignitionProb = prob(cell, params, pos._1, pos._2, matrix)
    val (randVal, nextRng) = r.nextDouble
    if randVal < ignitionProb then
      val newBurningCell = CellType.Burning(
        currentCycle,
        FireStage.Ignition,
        matrix(pos._1)(pos._2)
      )
      (ignitedAcc + (pos -> newBurningCell), nextRng)
    else
      (ignitedAcc, nextRng)
  }
