package it.unibo.firesim.controller

import it.unibo.firesim.model.CellType
import it.unibo.firesim.model.fire.FireStage.Ignition

object CellTypeConverter:

  def toModel(
      viewType: CellViewType,
      prevCell: CellType = CellType.Forest,
      currentGeneration: Int = 0
  ): CellType = viewType match
    case CellViewType.Fire =>
      CellType.Burning(currentGeneration, Ignition, prevCell)
    case CellViewType.Grass       => CellType.Grass
    case CellViewType.Forest      => CellType.Forest
    case CellViewType.Empty       => CellType.Empty
    case CellViewType.Station     => CellType.Station
    case CellViewType.Burnt       => CellType.Burnt
    case CellViewType.Rock        => CellType.Rock
    case CellViewType.Firefighter => CellType.Firefighter
    case CellViewType.Water       => CellType.Water
    case null                     =>
      throw new IllegalArgumentException(s"Unknown CellViewType: $viewType")

  def toView(modelType: CellType): CellViewType = modelType match
    case CellType.Burning(_, _, _) => CellViewType.Fire
    case CellType.Grass            => CellViewType.Grass
    case CellType.Forest           => CellViewType.Forest
    case CellType.Empty            => CellViewType.Empty
    case CellType.Station          => CellViewType.Station
    case CellType.Burnt            => CellViewType.Burnt
    case CellType.Rock             => CellViewType.Rock
    case CellType.Firefighter      => CellViewType.Firefighter
    case CellType.Water            => CellViewType.Water
    case null                      =>
      throw new IllegalArgumentException(s"Unknown CellType: $modelType")
