package it.unibo.firesim.controller

import it.unibo.firesim.config.UIConfig.{burntSoilStr, emptySoilStr, fireSoilStr, forestSoilStr, grassSoilStr, stationSoilStr}

enum CellViewType(val soilType: String):
  case Fire extends CellViewType(fireSoilStr)
  case Grass extends CellViewType(grassSoilStr)
  case Forest extends CellViewType(forestSoilStr)
  case Empty extends CellViewType(emptySoilStr)
  case Burnt extends CellViewType(burntSoilStr)
  case Station extends CellViewType(stationSoilStr)

object CellViewType:
  def fromString(s: String): Option[CellViewType] =
    values.find(_.soilType == s)