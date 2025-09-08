package it.unibo.firesim.model.fire

import it.unibo.firesim.config.Config.*

/** Represents different types of vegetation, each with distinct fire
  * properties.
  * @param flammability
  *   A base factor for ignition probability.
  * @param burnDuration
  *   The number of simulation cycles it takes for this vegetation to burn out.
  */
enum Vegetation(val flammability: Double, val burnDuration: Int):
  case Forest extends Vegetation(forestFlammability, forestBurnDuration)
  case Grass extends Vegetation(grassFlammability, grassBurnDuration)
  case None extends Vegetation(noFlammability, noBurnDuration)
