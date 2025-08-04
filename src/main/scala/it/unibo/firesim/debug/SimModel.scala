package it.unibo.firesim.debug

case class Params(
    windSpeed: Double,
    windAngle: Double,
    temperature: Double,
    humidity: Double
)

type SimMap = Vector[Vector[Int]]

class MockSimModel:
  private var params: Params = Params(1.0, 0.0, 25.0, 50.0)

  def getSimParams: Params = params

  def setWindSpeed(speed: Double): Unit =
    params = params.copy(windSpeed = speed)

  def setWindAngle(angle: Double): Unit =
    params = params.copy(windAngle = angle)

  def setTemperature(temp: Double): Unit =
    params = params.copy(temperature = temp)

  def setHumidity(humidity: Double): Unit =
    params = params.copy(humidity = humidity)
