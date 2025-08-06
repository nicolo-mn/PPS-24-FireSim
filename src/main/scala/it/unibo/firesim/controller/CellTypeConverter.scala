package it.unibo.firesim.controller

import it.unibo.firesim.model.cell.CellType

object CellTypeConverter:
  def toModel(viewType: CellViewType): CellType = viewType match
//    case CellViewType.Fire => CellType.Burning(0)
    case CellViewType.Grass => CellType.Grass
    case CellViewType.Forest => CellType.Forest
    case CellViewType.Empty => CellType.Empty
    case CellViewType.Station => CellType.Station
//    case CellViewType.Burnt => CellType.Burnt
    case _ => throw new IllegalArgumentException(s"Unknown CellViewType: $viewType")