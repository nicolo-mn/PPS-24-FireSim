package it.unibo.firesim.model.fire

import it.unibo.firesim.model.CellType

extension (cell: CellType)

  /** Extracts the vegetation type associated with the given cell.
    * @return
    *   the corresponding Vegetation type.
    */
  def vegetation: Vegetation = cell match
    case CellType.Forest => Vegetation.Forest
    case CellType.Grass  => Vegetation.Grass
    case _               => Vegetation.None

  /** Checks whether the given cell can catch fire.
    * @return
    *   true if the cell's flammability is greater than 0, false otherwise.
    */
  def isFlammable: Boolean =
    vegetation.flammability > 0

  /** Checks if a cell is currently burning.
    * @return
    *   true if the cell is a `CellType.Burning` instance, false otherwise.
    */
  def isBurning: Boolean = cell match
    case CellType.Burning(_, _, _) => true
    case _                         => false

  def isWater: Boolean = cell match
    case CellType.Water => true
    case _ => false
