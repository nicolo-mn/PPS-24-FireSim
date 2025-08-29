package it.unibo.firesim.model.fire

enum Vegetation(val flammability: Double, val burnDuration: Int):
  case Forest extends Vegetation(flammability = 0.02, burnDuration = 100)
  case Grass extends Vegetation(flammability = 0.05, burnDuration = 70)
  case None extends Vegetation(flammability = 0.0, burnDuration = 0)
