package it.unibo.firesim.view

import scala.swing.*
import it.unibo.firesim.config.UIConfig.*
import it.unibo.firesim.controller.{CellViewType, SimController, SpeedType}

import scala.swing.event.{ButtonClicked, MouseDragged, MousePressed, SelectionChanged, ValueChanged, WindowClosing}
import java.awt.{Color, Dimension}
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.swing.MenuBar.NoMenuBar.{listenTo, repaint}

extension (i: Int)
  /** Clamps an integer value within a given range. */
  private def clamp(lo: Int, hi: Int): Int = math.max(lo, math.min(hi, i))

/** Utility methods for GridCanvas.
  */
private object GridCanvas:

  /** The color associated with a given cell type
    * @param cell
    *   the cell type to find the color
    * @return
    *   the color associated with a given cell type
    */
  private def cellColor(cell: CellViewType): Color = cell match
    case CellViewType.Fire        => Color.red
    case CellViewType.Empty       => Color.white
    case CellViewType.Forest      => Color.green.darker()
    case CellViewType.Grass       => Color.green.brighter()
    case CellViewType.Station     => Color.yellow
    case CellViewType.Burnt       => Color.gray.darker()
    case CellViewType.Rock        => Color.gray.brighter()
    case CellViewType.Firefighter => new Color(165, 42, 42)
    case CellViewType.Water       => Color.blue
    case null                     => Color.lightGray

class GridCanvas(
    var gridSize: Int,
    private var gridData: Seq[CellViewType],
    private val onClick: ((Int, Int)) => Unit,
    private val onHover: ((Int, Int)) => Unit
) extends Panel:

  private case class GridGeometry(cellSize: Int, offsetX: Int, offsetY: Int)
  listenTo(mouse.clicks, mouse.moves)

  reactions += {
    case e: MousePressed => onClick(pixelToCell(e.point))
    case e: MouseDragged => onHover(pixelToCell(e.point))
  }

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)
    val geo = gridGeometry
    for row <- 0 until gridSize; col <- 0 until gridSize do
      val idx = row * gridSize + col
      g.setColor(GridCanvas.cellColor(gridData(idx)))
      g.fillRect(
        col * geo.cellSize + geo.offsetX,
        row * geo.cellSize + geo.offsetY,
        geo.cellSize,
        geo.cellSize
      )
      g.setColor(Color.black)
      g.drawRect(
        col * geo.cellSize + geo.offsetX,
        row * geo.cellSize + geo.offsetY,
        geo.cellSize,
        geo.cellSize
      )

  /** Converts a pixel coordinate to a grid cell coordinate. */
  private def pixelToCell(p: Point): (Int, Int) =
    val geo = gridGeometry
    (
      ((p.y - geo.offsetY) / geo.cellSize).clamp(0, gridSize - 1),
      ((p.x - geo.offsetX) / geo.cellSize).clamp(0, gridSize - 1)
    )

  private def gridGeometry: GridGeometry =
    val cellSize = (math.min(size.width, size.height) max 1) / gridSize
    val offsetX = (size.width - (cellSize * gridSize)) / 2
    val offsetY = (size.height - (cellSize * gridSize)) / 2
    GridGeometry(cellSize, offsetX, offsetY)

  /** Updates the grid data and repaints the canvas.
    * @param updated
    *   the sequence containing all the Cell
    */
  def updateGrid(updated: Seq[CellViewType]): Unit =
    if updated.size == gridSize * gridSize then
      gridData = updated
      Swing.onEDT(repaint())

  /** Resets the grid to a new size, clearing all cells and repaints the canvas
    * @param newSize
    *   the new size of the grid
    */
  def reset(newSize: Int): Unit =
    gridSize = newSize
    gridData = Seq.fill(gridSize * gridSize)(CellViewType.Empty)
    Swing.onEDT(repaint())

class SimView(private val simController: SimController):
  private var gridSize: Int = askForGridSize()
  simController.generateMap(gridSize, gridSize)

  private val gridData: Seq[CellViewType] =
    Seq.fill(gridSize * gridSize)(CellViewType.Empty)

  private val gridCanvas =
    new GridCanvas(gridSize, gridData, handleClick, handleHover)

  gridCanvas.preferredSize = new Dimension(500, 500)

  private val speedSelector = new ComboBox(SpeedType.values.toSeq):
    renderer = ListView.Renderer(_.id)

  speedSelector.selection.item = SpeedType.Speed1x
  speedSelector.listenTo(speedSelector.selection)
  speedSelector.reactions += {
    case SelectionChanged(_) =>
      simController.updateSimulationSpeed(
        speedSelector.selection.item.multiplier
      )
  }

  private val mapEditAvailableSoils =
    Seq(
      fireSoilStr,
      emptySoilStr,
      forestSoilStr,
      grassSoilStr,
      rockSoilStr,
      waterSoilStr
    )

  private val inGameAvailableSoils = Seq(fireSoilStr, emptySoilStr)
  private val soilTypeSelector = new ComboBox(mapEditAvailableSoils)

  private var firstClick: Option[(Int, Int)] = None
  private val drawLineButton: ToggleButton = new ToggleButton("🖉 Draw Line")

  drawLineButton.reactions += {
    case ButtonClicked(_) =>
      firstClick = None
  }

  private val brushToggle: ToggleButton = new ToggleButton("Brush")

  // controlli
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
      brushToggle.enabled = false
      soilTypeSelector.peer.setModel(
        ComboBox.newConstantModel(inGameAvailableSoils)
      )
      soilTypeSelector.selection.item = fireSoilStr
      Future {
        simController.startSimulation()
      }(ExecutionContext.global)
  }

  resetButton.reactions += {
    case ButtonClicked(_) =>
      resetButton.enabled = false
      startButton.enabled = true
      pauseResumeButton.enabled = false
      brushToggle.enabled = true
      brushToggle.selected = false
      soilTypeSelector.peer.setModel(
        ComboBox.newConstantModel(mapEditAvailableSoils)
      )
      soilTypeSelector.selection.item = fireSoilStr
      // TODO: notify controller
      simController.stopSimulation()
      gridSize = askForGridSize()
      gridCanvas.reset(gridSize)
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
    contents += speedSelector
    contents += soilTypeSelector
    contents += drawLineButton
    contents += brushToggle
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
      layout(gridCanvas) = BorderPanel.Position.Center
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
    if updatedGridCells.length == gridSize * gridSize then
      gridCanvas.updateGrid(updatedGridCells)

  @tailrec
  private def askForGridSize(): Int =
    Dialog.showInput(null, "Enter grid size:", initial = "10") match
      case None => sys.exit(0)
      case Some(input)
          if input.toIntOption.isDefined && input.toInt >= minimumGridSize =>
        input.toInt
      case Some(_) =>
        Dialog.showMessage(
          null,
          s"Grid size must be a number greater than ${minimumGridSize - 1}.",
          title = "Input Error",
          Dialog.Message.Error
        )
        askForGridSize()

  private def handleClick(pos: (Int, Int)): Unit =
    val selectedType = CellViewType.fromString(
      soilTypeSelector.item
    ).getOrElse(CellViewType.Empty)
    if drawLineButton.selected then
      handleDrawLine(pos, selectedType)
    else
      simController.placeCell(pos, selectedType)

  private def handleHover(pos: (Int, Int)): Unit =
    if brushToggle.selected then
      val selectedType = CellViewType.fromString(
        soilTypeSelector.item
      ).getOrElse(CellViewType.Empty)
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
        Future {
          simController.placeLine(start, pos, cellViewType)
        }(ExecutionContext.global)
