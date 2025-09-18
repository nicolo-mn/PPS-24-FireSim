package it.unibo.firesim.view

import it.unibo.firesim.controller.CellViewType

/** Represents the Graphical User Interface
  */
trait View:

  /** Updates the view map
    *
    * @param updatedGridCells
    *   sequence of cells representing the updated map
    */
  def setViewMap(updatedGridCells: Seq[CellViewType]): Unit
