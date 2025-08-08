package it.unibo.firesim.model.fire

import it.unibo.firesim.model.cell.{Cell, CellType}
import it.unibo.firesim.model.fire.{ProbabilityCalc, BurnDurationPolicy, RandomProvider}

private def baseFlammability(cell: Cell): Double =
  cell.cellType match
    case CellType.Forest => 0.6
    case CellType.Grass  => 0.3
    case _               => 0.0

given defaultProbabilityCalc: ProbabilityCalc = (cell, params, r, c, matrix) =>
  val base = baseFlammability(cell)
  val humidityFactor = 1.0 - params.humidity / 100.0
  val temperatureFactor = (params.temperature - 15.0) / 25.0
  base * humidityFactor * temperatureFactor

given defaultBurnDuration: BurnDurationPolicy = (start, current) =>
  (current - start) >= 3

given defaultRandomProvider: RandomProvider = () =>
  scala.util.Random.nextDouble()
