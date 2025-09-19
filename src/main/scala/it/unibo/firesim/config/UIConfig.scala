package it.unibo.firesim.config

import java.awt.Color

object UIConfig:
  val minWidth: Int = 700
  val minHeight: Int = 700
  val defaultWidth: Int = 1000
  val defaultHeight: Int = 1000
  val defaultGridCanvasWidth: Int = 500
  val defaultGridCanvasHeight: Int = 500
  val defaultScrollPanelWidth: Int = 200
  val defaultScrollPanelHeight: Int = 200
  val defaultControlsPanelWidth: Int = 500
  val defaultControlsPanelHeight: Int = 0
  val minControlsPanelWidth: Int = 500
  val minControlsPanelHeight: Int = 0
  val emptySpaceDim: Int = 5
  val defaultGridSize: Int = 10
  val minHumidity: Int = 0
  val maxHumidity: Int = 100
  val defaultHumidity: Int = 50
  val minTemperature: Int = -20
  val maxTemperature: Int = 50
  val defaultTemperature: Int = 20
  val minWindDirection: Int = 0
  val maxWindDirection: Int = 360
  val defaultWindDirection: Int = 90
  val minWindIntensity: Int = 0
  val maxWindIntensity: Int = 50
  val defaultWindIntensity: Int = 20
  val humidityLabelText: String = "Current humidity: "
  val humidityUnit: String = " %"
  val temperatureLabelText: String = "Current temperature: "
  val temperatureUnit: String = " °C"
  val windDirectionLabelText: String = "Current wind angle: "
  val windDirectionUnit: String = "°"
  val windIntensityLabelText: String = "Current wind speed: "
  val windIntensityUnit: String = " km/s"
  val backgroundColor: Color = Color.white
  val fireSoilStr: String = "Fire\uD83D\uDD25"
  val grassSoilStr: String = "Grass\uD83C\uDF31"
  val forestSoilStr: String = "Forest\uD83C\uDF32"
  val emptySoilStr: String = "Barrier\uD83D\uDED1"
  val burntSoilStr: String = "Burnt"
  val stationSoilStr: String = "Station\uD83D\uDE81"
  val rockSoilStr: String = "Rock\u26F0"
  val firefighterStr: String = "Firefighter"
  val waterSoilStr: String = "Water\uD83D\uDCA7"
  val minGridSize: Int = 6
  val maxGridSize: Int = 150
