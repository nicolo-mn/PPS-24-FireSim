package it.unibo.firesim.view

import scala.swing.*
import it.unibo.firesim.config.UIConfig.*

import scala.swing.event.{ButtonClicked, ValueChanged}
import java.awt.{Color, Dimension}
import javax.swing.JPanel
import scala.annotation.tailrec

class GridButton(
    var color: Color,
    private val soilTypeSelector: ComboBox[String]
) extends Button:
  background = color
  reactions += {
    case ButtonClicked(_) =>
      // TODO: notify controller instead
      color = Color.red
      repaint()
  }

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)
    g.setColor(color)
    g.fillRect(0, 0, size.width, size.height)

class SimView:
  private val gridSize: Int = askForGridSize()

  // TODO: notify controller
  private val mapEditAvailableSoils =
    Seq(fireSoilStr, emptySoilStr, forestSoilStr, grassSoilStr)

  private val inGameAvailableSoils = Seq(fireSoilStr, emptySoilStr)
  private val soilTypeSelector = new ComboBox(mapEditAvailableSoils)
  soilTypeSelector.selection.item = fireSoilStr

  private val gridCells: Seq[GridButton] = Seq.fill(
    gridSize * gridSize
  )(new GridButton(Color.white, soilTypeSelector))

  private val gridPanel = new GridPanel(gridSize, gridSize):
    contents ++= gridCells
    // The wrapped peer needs to be overridden as the grid's parent bypasses the homonymous scala method to handle resizing
    override lazy val peer: JPanel =
      new JPanel(new java.awt.GridLayout(gridSize, gridSize)):
        // Handle grid resize
        override def getPreferredSize: Dimension =
          val d = super.getPreferredSize
          val c = getParent
          val prefSize =
            if c == null then new Dimension(d.width, d.height)
            else c.getSize
          val w = prefSize.getWidth.toInt
          val h = prefSize.getHeight.toInt
          val s = if w > h then h else w
          new Dimension(s, s)
    override def preferredSize: Dimension = peer.getPreferredSize

  // Put the grid as the only element of a GridBagPanel to keep it centered
  private val boardConstraint: Panel = new GridBagPanel:
    background = backgroundColor
    layout(gridPanel) = new Constraints

  private val startButton: Button = new Button("▶ Start")
  private val pauseResumeButton: Button = new Button("⏸ Pause/Resume")
  private val resetButton: Button = new Button("\uD83D\uDD04 Reset")
  pauseResumeButton.enabled = false
  resetButton.enabled = false

  startButton.reactions += {
    case ButtonClicked(_) =>
      startButton.enabled = false
      pauseResumeButton.enabled = true
      resetButton.enabled = true
      soilTypeSelector.peer.setModel(
        ComboBox.newConstantModel(inGameAvailableSoils)
      )
      soilTypeSelector.selection.item = fireSoilStr
    // TODO: notify controller
  }

  resetButton.reactions += {
    case ButtonClicked(_) =>
      resetButton.enabled = false
      startButton.enabled = true
      pauseResumeButton.enabled = false
      soilTypeSelector.peer.setModel(
        ComboBox.newConstantModel(mapEditAvailableSoils)
      )
      soilTypeSelector.selection.item = fireSoilStr
    // TODO: notify controller
  }

  pauseResumeButton.reactions += {
    case ButtonClicked(_) =>
    // TODO: notify controller
  }

  private val humidityLabel =
    new Label(humidityLabelText + defaultHumidity + humidityUnit)

  private val temperatureLabel =
    new Label(temperatureLabelText + defaultTemperature + temperatureUnit)

  private val windDirectionLabel =
    new Label(windDirectionLabelText + defaultWindDirection + windDirectionUnit)

  private val windIntensityLabel =
    new Label(windIntensityLabelText + defaultWindIntensity + windIntensityUnit)

  private val humiditySlider = new Slider:
    min = minHumidity
    max = maxHumidity
    value = defaultHumidity
    reactions += {
      case ValueChanged(_) =>
        humidityLabel.text = humidityLabelText + value + humidityUnit
      // TODO: handle value changes
    }

  private val temperatureSlider: Slider = new Slider:
    min = minTemperature
    max = maxTemperature
    value = defaultTemperature
    reactions += {
      case ValueChanged(_) =>
        temperatureLabel.text = temperatureLabelText + value + temperatureUnit
      // TODO: handle value changes
    }

  private val windDirectionSlider: Slider = new Slider:
    min = minWindDirection
    max = maxWindDirection
    value = defaultWindDirection
    reactions += {
      case ValueChanged(_) =>
        windDirectionLabel.text =
          windDirectionLabelText + value + windDirectionUnit
      // TODO: handle value changes
    }

  private val windIntensitySlider: Slider = new Slider:
    min = minWindIntensity
    max = maxWindIntensity
    value = defaultWindIntensity
    reactions += {
      case ValueChanged(_) =>
        windIntensityLabel.text =
          windIntensityLabelText + value + windIntensityUnit
      // TODO: handle value changes
    }

  private val controlsPanel = new FlowPanel():
    contents += startButton
    contents += pauseResumeButton
    contents += resetButton
    contents += soilTypeSelector
    contents += new Label("Humidity:")
    contents += humiditySlider
    contents += humidityLabel
    contents += new Label("Temperature:")
    contents += temperatureSlider
    contents += temperatureLabel
    contents += new Label("Wind Direction:")
    contents += windDirectionSlider
    contents += windDirectionLabel
    contents += new Label("Wind Intensity:")
    contents += windIntensitySlider
    contents += windIntensityLabel
    border = Swing.EmptyBorder(0, 0, 20, 0)

  private val scrollPanel = new ScrollPane(controlsPanel):
    horizontalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
    verticalScrollBarPolicy = ScrollPane.BarPolicy.Never

  new MainFrame:
    title = "FireSim"
    preferredSize = new Dimension(defaultWidth, defaultHeight)
    minimumSize = new Dimension(minWidth, minHeight)
    contents = new BorderPanel:
      layout(scrollPanel) = BorderPanel.Position.North
      layout(boardConstraint) = BorderPanel.Position.Center
    centerOnScreen()
    visible = true

  def setViewMap(updatedColors: Seq[Color]): Unit =
    if updatedColors.length != gridCells.length then
      // TODO: log error
      return
    else
      gridCells.zip(updatedColors).foreach((b, c) =>
        b.color = c; b.repaint()
      )
      
  //TODO!! Traduci da stringa a colore

  @tailrec
  private def askForGridSize(): Int =
    Dialog.showInput(null, "Enter grid size:", initial = "10") match
      case None => sys.exit(0)
      case Some(input)
          if input.toIntOption.isDefined && input.toInt >= minimumGridSize =>
        input.toInt
      case Some(input) =>
        Dialog.showMessage(
          null,
          s"Grid size must be a number greater than ${minimumGridSize - 1}.",
          title = "Input Error",
          Dialog.Message.Error
        )
        askForGridSize()
