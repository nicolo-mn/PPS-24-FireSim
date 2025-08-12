package it.unibo.firesim.controller

import it.unibo.firesim.model.cell.CellType

object CellTypeConverter:

  def toModel(viewType: CellViewType): CellType = viewType match
    case CellViewType.Fire        => CellType.Burning(0)
    case CellViewType.Grass       => CellType.Grass
    case CellViewType.Forest      => CellType.Forest
    case CellViewType.Empty       => CellType.Empty
    case CellViewType.Station     => CellType.Station
    case CellViewType.Burnt       => CellType.Burnt
    case CellViewType.Rock        => CellType.Rock
    case CellViewType.Firefighter => CellType.Firefighter
    case null                     =>
      throw new IllegalArgumentException(s"Unknown CellViewType: $viewType")

  def toView(modelType: CellType): CellViewType = modelType match
    case CellType.Burning(_)  => CellViewType.Fire
    case CellType.Grass       => CellViewType.Grass
    case CellType.Forest      => CellViewType.Forest
    case CellType.Empty       => CellViewType.Empty
    case CellType.Station     => CellViewType.Station
    case CellType.Burnt       => CellViewType.Burnt
    case CellType.Rock        => CellViewType.Rock
    case CellType.Firefighter => CellViewType.Firefighter
    case null                 =>
      throw new IllegalArgumentException(s"Unknown CellType: $modelType")
