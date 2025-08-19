package it.unibo.firesim.controller

import it.unibo.firesim.config.UIConfig.{burntSoilStr, emptySoilStr, fireSoilStr, firefighterStr, forestSoilStr, grassSoilStr, rockSoilStr, stationSoilStr, waterSoilStr}

enum CellViewType(val soilType: String):
  case Fire extends CellViewType(fireSoilStr)
  case Grass extends CellViewType(grassSoilStr)
  case Forest extends CellViewType(forestSoilStr)
  case Empty extends CellViewType(emptySoilStr)
  case Burnt extends CellViewType(burntSoilStr)
  case Station extends CellViewType(stationSoilStr)
  case Rock extends CellViewType(rockSoilStr)
  case Firefighter extends CellViewType(firefighterStr)
  case Water extends CellViewType(waterSoilStr)

object CellViewType:

  def fromString(s: String): Option[CellViewType] =
    values.find(_.soilType == s)
