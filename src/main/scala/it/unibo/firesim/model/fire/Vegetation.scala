package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*

enum Vegetation(val flammability: Double, val burnDuration: Int):
  case Forest extends Vegetation(forestFlammability, forestBurnDuration)
  case Grass extends Vegetation(grassFlammability, grassBurnDuration)
  case None extends Vegetation(noFlammability, noBurnDuration)
