package it.unibo.firesim.model.map

import it.unibo.firesim.model.fire.FireStage

enum CellType:
  case Forest, Grass, Empty, Station, Burnt, Rock, Firefighter, Water
  case Burning(startCycle: Int, fireStage: FireStage, originalType: CellType)
