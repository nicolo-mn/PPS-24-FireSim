package it.unibo.firesim.model

/** Simulation parameters for fire propagation.
  *
  * @param windSpeed
  *   The speed of the wind
  * @param windAngle
  *   The wind direction in degrees
  * @param temperature
  *   The ambient temperature
  * @param humidity
  *   The relative humidity
  */
case class SimParams(
    windSpeed: Int,
    windAngle: Int,
    temperature: Int,
    humidity: Int
)
