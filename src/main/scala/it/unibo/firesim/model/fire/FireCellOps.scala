package it.unibo.firesim.model.fire

import it.unibo.firesim.model.CellType

object FireCellOps:

  def vegetation(cell: CellType): Vegetation = cell match
    case CellType.Forest => Vegetation.Forest
    case CellType.Grass  => Vegetation.Grass
    case _               => Vegetation.None

  def isFlammable(cell: CellType): Boolean =
    vegetation(cell).flammability > 0

  def isBurning(cell: CellType): Boolean = cell match
    case CellType.Burning(_, _, _) => true
    case _                         => false

extension (cell: CellType)
  def isFlammable: Boolean = FireCellOps.isFlammable(cell)
  def isBurning: Boolean = FireCellOps.isBurning(cell)
  def vegetation: Vegetation = FireCellOps.vegetation(cell)
