package it.unibo.firesim.debug

import it.unibo.firesim.model.SimModel
import it.unibo.firesim.model.cell.CellType

/** * A simple console renderer to debug the simulated forest map.
  */
object MapRenderer extends App:
  val model = SimModel()
  val matrix = model.generateMap(60, 90)

  private def render(cellType: CellType): String = cellType match
    case CellType.Forest  => Console.GREEN + "F" + Console.RESET
    case CellType.Grass   => Console.GREEN + "G" + Console.RESET
    case CellType.Empty   => Console.WHITE + "E" + Console.RESET
    case CellType.Station => Console.YELLOW + "S" + Console.RESET
    case _                => Console.WHITE + "x" + Console.RESET

  matrix.foreach { row =>
    println(row.map(cell => render(cell.cellType)).mkString(" "))
  }
