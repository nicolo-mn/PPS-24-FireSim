package it.unibo.firesim.debug

import it.unibo.firesim.model.SimModel
import it.unibo.firesim.model.cell.CellType

/** * A simple console renderer to debug the simulated forest map.
 */
object MapRenderer extends App {
  val model = SimModel()
  val matrix = model.generateMap(60, 90)

  private def render(cellType: CellType): String = cellType match {
    case CellType.Forest   => Console.GREEN + "░" + Console.RESET
    case CellType.Grass    => Console.GREEN + "▓" + Console.RESET
    case CellType.Empty    => Console.WHITE + "□" + Console.RESET
    case CellType.Station  => Console.YELLOW + "■" + Console.RESET
  }

  matrix.cells.foreach { row =>
    println(row.map(cell => render(cell.cellType)).mkString(" "))
  }
}
