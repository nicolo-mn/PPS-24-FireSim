package it.unibo.firesim.view

import it.unibo.firesim.config.UIConfig.*
import it.unibo.firesim.controller.{SimController, SpeedType}
import scala.concurrent.{ExecutionContext, Future}
import scala.swing.*
import scala.swing.event.{ButtonClicked, SelectionChanged, ValueChanged}

class ControlsPanel(simController: SimController) extends FlowPanel:

  private val mapEditAvailableSoils = Seq(
    fireSoilStr,
    emptySoilStr,
    forestSoilStr,
    grassSoilStr,
    rockSoilStr,
    waterSoilStr
  )

  private val inGameAvailableSoils = Seq(fireSoilStr, emptySoilStr)

  val soilTypeSelector = new ComboBox(mapEditAvailableSoils)
  val drawLineButton: ToggleButton = new ToggleButton("🖉 Draw Line")
  val brushToggle: ToggleButton = new ToggleButton("Brush")
  val resetButton: Button = new Button("\uD83D\uDD04 Reset")

  private val startButton: Button = new Button("▶ Start")
  private val pauseResumeButton: Button = new Button("⏸ Pause")

  private val speedSelector = new ComboBox(SpeedType.values.toSeq):
    renderer = ListView.Renderer(_.id)

  private val humidityLabel =
    new Label(humidityLabelText + defaultHumidity + humidityUnit)

  private val temperatureLabel =
    new Label(temperatureLabelText + defaultTemperature + temperatureUnit)

  private val windDirectionLabel =
    new Label(windDirectionLabelText + defaultWindDirection + windDirectionUnit)

  private val windIntensityLabel =
    new Label(windIntensityLabelText + defaultWindIntensity + windIntensityUnit)

  private val humiditySlider = new Slider:
    min = minHumidity; max = maxHumidity; value = defaultHumidity

  private val temperatureSlider: Slider = new Slider:
    min = minTemperature; max = maxTemperature; value = defaultTemperature

  private val windDirectionSlider: Slider = new Slider:
    min = minWindDirection; max = maxWindDirection; value = defaultWindDirection

  private val windIntensitySlider: Slider = new Slider:
    min = minWindIntensity; max = maxWindIntensity; value = defaultWindIntensity

  pauseResumeButton.enabled = false
  resetButton.enabled = false
  speedSelector.selection.item = SpeedType.Speed1x
  soilTypeSelector.selection.item = fireSoilStr
  border = Swing.EmptyBorder(0, 0, 20, 0)

  contents ++= Seq(
    startButton,
    pauseResumeButton,
    resetButton,
    speedSelector,
    soilTypeSelector,
    drawLineButton,
    brushToggle,
    new Label("Humidity:"),
    humiditySlider,
    humidityLabel,
    new Label("Temperature:"),
    temperatureSlider,
    temperatureLabel,
    new Label("Wind Direction:"),
    windDirectionSlider,
    windDirectionLabel,
    new Label("Wind Intensity:"),
    windIntensitySlider,
    windIntensityLabel
  )

  listenTo(
    startButton,
    pauseResumeButton,
    resetButton,
    speedSelector.selection,
    soilTypeSelector.selection,
    drawLineButton,
    brushToggle,
    humiditySlider,
    temperatureSlider,
    windDirectionSlider,
    windIntensitySlider
  )

  reactions += {
    case ButtonClicked(`startButton`) =>
      onStart()

    case ButtonClicked(`resetButton`) =>
      resetButton.enabled = false
      startButton.enabled = true
      pauseResumeButton.enabled = false
      pauseResumeButton.text = "⏸ Pause"
      brushToggle.selected = false
      soilTypeSelector.peer.setModel(
        ComboBox.newConstantModel(mapEditAvailableSoils)
      )
      soilTypeSelector.selection.item = fireSoilStr
      simController.stopSimulation()

    case ButtonClicked(`pauseResumeButton`) =>
      simController.pauseResumeSimulation()
      val isNowPaused = pauseResumeButton.text == "⏸ Pause"
      pauseResumeButton.text = if isNowPaused then "▶ Resume" else "⏸ Pause"

    case ButtonClicked(`drawLineButton`) =>
      if drawLineButton.selected then
        brushToggle.selected = false
        brushToggle.enabled = false
      else
        brushToggle.enabled = true

    case ButtonClicked(`brushToggle`) =>
      if brushToggle.selected then
        drawLineButton.enabled = false
      else
        drawLineButton.enabled = true

    case SelectionChanged(`speedSelector`) =>
      simController.updateSimulationSpeed(
        speedSelector.selection.item.multiplier
      )

    case ValueChanged(`humiditySlider`) =>
      humidityLabel.text =
        humidityLabelText + humiditySlider.value + humidityUnit
      simController.setHumidity(humiditySlider.value)

    case ValueChanged(`temperatureSlider`) =>
      temperatureLabel.text =
        temperatureLabelText + temperatureSlider.value + temperatureUnit
      simController.setTemperature(temperatureSlider.value)

    case ValueChanged(`windDirectionSlider`) =>
      windDirectionLabel.text =
        windDirectionLabelText + windDirectionSlider.value + windDirectionUnit
      simController.setWindAngle(windDirectionSlider.value)

    case ValueChanged(`windIntensitySlider`) =>
      windIntensityLabel.text =
        windIntensityLabelText + windIntensitySlider.value + windIntensityUnit
      simController.setWindSpeed(windIntensitySlider.value)
  }

  private def onStart(): Unit =
    startButton.enabled = false
    pauseResumeButton.enabled = true
    resetButton.enabled = true
    drawLineButton.selected = false
    brushToggle.enabled = true
    brushToggle.selected = false
    soilTypeSelector.peer.setModel(
      ComboBox.newConstantModel(inGameAvailableSoils)
    )
    soilTypeSelector.selection.item = fireSoilStr
    Future { simController.startSimulation() }(ExecutionContext.global)
