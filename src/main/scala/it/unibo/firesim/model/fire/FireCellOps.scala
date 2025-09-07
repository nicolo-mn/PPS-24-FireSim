package it.unibo.firesim.model.fire

import it.unibo.firesim.model.CellType

/** A utility object containing operations related to CellType.
  */
object FireCellOps:

  /** Extracts the vegetation type associated with the given cell.
    * @param cell
    *   the cell to analyze.
    * @return
    *   the corresponding Vegetation type.
    */
  def vegetation(cell: CellType): Vegetation = cell match
    case CellType.Forest => Vegetation.Forest
    case CellType.Grass  => Vegetation.Grass
    case _               => Vegetation.None

  /** Checks whether the given cell can catch fire.
    * @param cell
    *   the cell to analyze.
    * @return
    *   true if the cell's flammability is greater than 0, false otherwise.
    */
  def isFlammable(cell: CellType): Boolean =
    vegetation(cell).flammability > 0

  /** Checks if a cell is currently burning.
    * @param cell
    *   the cell to analyze.
    * @return
    *   true if the cell is a `CellType.Burning` instance, false otherwise.
    */
  def isBurning(cell: CellType): Boolean = cell match
    case CellType.Burning(_, _, _) => true
    case _                         => false

extension (cell: CellType)
  def isFlammable: Boolean = FireCellOps.isFlammable(cell)
  def isBurning: Boolean = FireCellOps.isBurning(cell)
  def vegetation: Vegetation = FireCellOps.vegetation(cell)
