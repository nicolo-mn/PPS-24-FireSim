package it.unibo.firesim.view

import scala.swing.*
import it.unibo.firesim.config.UIConfig.*
import it.unibo.firesim.controller.{CellViewType, SimController}

import scala.swing.event.{ButtonClicked, ValueChanged, WindowClosing, SelectionChanged}
import java.awt.{Color, Dimension}
import javax.swing.JPanel
import scala.annotation.tailrec
import scala.swing.MenuBar.NoMenuBar.listenTo

class GridButton(
    private val pos: (Int, Int),
    private val onClick: ((Int, Int)) => Unit,
    var color: Color
) extends Button:
  background = color
  reactions += {
    case ButtonClicked(_) => onClick(pos)
  }

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)
    g.setColor(color)
    g.fillRect(0, 0, size.width, size.height)

class SimView(private val simController: SimController):
  private var gridSize: Int = askForGridSize()
  // TODO: notify controller
  simController.generateMap(gridSize, gridSize)

  private val mapEditAvailableSoils =
    Seq(fireSoilStr, emptySoilStr, forestSoilStr, grassSoilStr, rockSoilStr)

  private val inGameAvailableSoils = Seq(fireSoilStr, emptySoilStr)
  private val soilTypeSelector = new ComboBox(mapEditAvailableSoils)
  soilTypeSelector.selection.item = fireSoilStr
  soilTypeSelector.listenTo(soilTypeSelector.selection)
  soilTypeSelector.reactions += {
    case SelectionChanged(_) =>
      firstClick = None
  }

  var gridCells: Seq[Seq[GridButton]] = Seq.empty

  private var drawLineMode = false
  private var firstClick: Option[(Int, Int)] = None
  private val drawLineButton: ToggleButton = new ToggleButton("🖉 Draw Line")

  drawLineButton.reactions += {
    case ButtonClicked(_) =>
      drawLineMode = drawLineButton.selected
      firstClick = None // reset eventuale primo click precedente
  }

  private val gridPanel = new GridPanel(gridSize, gridSize):
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

  generateGrid(gridSize, gridSize)

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
      simController.startSimulation()
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
      simController.stopSimulation()
      gridSize = askForGridSize()
      generateGrid(gridSize, gridSize)
      simController.generateMap(gridSize, gridSize)
  }

  pauseResumeButton.reactions += {
    case ButtonClicked(_) =>
      // TODO: notify controller
      simController.pauseResumeSimulation()
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
        simController.setHumidity(value)
    }

  private val temperatureSlider: Slider = new Slider:
    min = minTemperature
    max = maxTemperature
    value = defaultTemperature
    reactions += {
      case ValueChanged(_) =>
        temperatureLabel.text = temperatureLabelText + value + temperatureUnit
        // TODO: handle value changes
        simController.setTemperature(value)
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
        simController.setWindAngle(value)
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
        simController.setWindSpeed(value)
    }

  private val controlsPanel = new FlowPanel():
    contents += startButton
    contents += pauseResumeButton
    contents += resetButton
    contents += soilTypeSelector
    contents += drawLineButton
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

  private val mainFrame = new MainFrame:
    title = "FireSim"
    preferredSize = new Dimension(defaultWidth, defaultHeight)
    minimumSize = new Dimension(minWidth, minHeight)
    contents = new BorderPanel:
      layout(scrollPanel) = BorderPanel.Position.North
      layout(boardConstraint) = BorderPanel.Position.Center
    centerOnScreen()
    visible = true

  listenTo(mainFrame)
  mainFrame.reactions += {
    case _: WindowClosing =>
      val response = Dialog.showConfirmation(
        mainFrame,
        "Are you sure you want to quit the simulation?",
        optionType = Dialog.Options.YesNo,
        title = "Confirm Exit"
      )

      if response == Dialog.Result.Yes then
        simController.closing()
        mainFrame.dispose()
  }

  def setViewMap(updatedGridCells: Seq[CellViewType]): Unit =
    if updatedGridCells.length != gridSize * gridSize then
      // TODO: log error
      Dialog.showMessage(
        mainFrame,
        "Expected Seq[CellViewType] of length " + gridSize * gridSize
          + ", found " + updatedGridCells.length + "instead",
        messageType = Dialog.Message.Error,
        title = "ERROR"
      )
    else
      gridCells.flatten.zip(updatedGridCells).foreach((b, c) =>
        b.color = getCellColor(c); b.repaint()
      )

  private def getCellColor(cellViewType: CellViewType): Color =
    cellViewType match
      case CellViewType.Fire        => Color.red
      case CellViewType.Empty       => Color.white
      case CellViewType.Forest      => Color.green.darker()
      case CellViewType.Grass       => Color.green.brighter()
      case CellViewType.Station     => Color.yellow
      case CellViewType.Burnt       => Color.gray.darker()
      case CellViewType.Rock        => Color.gray.brighter()
      case CellViewType.Firefighter => new Color(165, 42, 42)
      case null                     => Color.lightGray

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

  private def handleClick(pos: (Int, Int)): Unit =
    val selectedType = CellViewType
      .fromString(soilTypeSelector.item)
      .getOrElse(CellViewType.Empty)

    if drawLineMode then
      handleDrawLine(pos, selectedType)
    else
      simController.placeCell(pos, selectedType)

  private def handleDrawLine(
      pos: (Int, Int),
      cellViewType: CellViewType
  ): Unit =
    firstClick match
      case None =>
        firstClick = Some(pos)
      case Some(start) =>
        firstClick = None
        simController.placeLine(start, pos, cellViewType)

  private def generateGrid(rows: Int, cols: Int): Unit =
    gridCells = Seq.tabulate(rows, cols) { (i, j) =>
      new GridButton((i, j), handleClick, Color.white)
    }

    gridPanel.contents.clear()
    gridPanel.peer.setLayout(new java.awt.GridLayout(rows, cols))
    gridPanel.contents ++= gridCells.flatten
    gridPanel.revalidate()
    gridPanel.repaint()
