package it.unibo.firesim.view

import it.unibo.firesim.config.UIConfig.*
import it.unibo.firesim.controller.*

import scala.swing.*
import scala.swing.event.{ButtonClicked, WindowClosing}
import java.awt.Dimension
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.swing.MenuBar.NoMenuBar.{listenTo, reactions}

class SimView(private val simController: SimController):

  private var firstClick: Option[(Int, Int)] = None

  private var gridSize: Int = askForGridSize()
  simController.handleViewMessage(GenerateMap(gridSize, gridSize))

  private val gridData: Seq[CellViewType] =
    Seq.fill(gridSize * gridSize)(CellViewType.Empty)

  private val controlsPanel = new ControlsPanel(simController)

  private val gridCanvas =
    new GridCanvas(gridSize, gridData, handleClick, handleHover)

  gridCanvas.preferredSize =
    new Dimension(defaultGridCanvasWidth, defaultGridCanvasHeight)

  private val scrollPanel = new ScrollPane(controlsPanel):
    horizontalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
    verticalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
    preferredSize =
      new Dimension(defaultScrollPanelWidth, defaultScrollPanelHeight)

  private val mainFrame = new Frame:
    title = "FireSim"
    preferredSize = new Dimension(defaultWidth, defaultHeight)
    minimumSize = new Dimension(minWidth, minHeight)
    contents = new SplitPane(Orientation.Horizontal, scrollPanel, gridCanvas):
      continuousLayout = true
      resizeWeight = 0.0
    centerOnScreen()
    visible = true
    peer.setDefaultCloseOperation(
      javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
    )

  listenTo(
    mainFrame,
    controlsPanel.resetButton,
    controlsPanel.soilTypeSelector.selection,
    controlsPanel.drawLineButton
  )

  reactions += {
    case _: WindowClosing =>
      onWindowClose()

    case ButtonClicked(b) if b == controlsPanel.resetButton =>
      simController.handleViewMessage(StopSimulation)
      val newSize = askForGridSize()
      gridSize = newSize
      simController.handleViewMessage(GenerateMap(gridSize, gridSize))
      gridCanvas.reset(gridSize)

    case ButtonClicked(b) if b == controlsPanel.drawLineButton =>
      firstClick = None

    case event.SelectionChanged(_) =>
      firstClick = None
  }

  private def onWindowClose(): Unit =
    val response = Dialog.showConfirmation(
      mainFrame,
      "Are you sure you want to quit the simulation?",
      optionType = Dialog.Options.YesNo,
      title = "Confirm Exit"
    )
    if response == Dialog.Result.Yes then
      simController.handleViewMessage(Closing)
      mainFrame.dispose()

  def setViewMap(updatedGridCells: Seq[CellViewType]): Unit =
    if updatedGridCells.length == gridSize * gridSize then
      gridCanvas.updateGrid(updatedGridCells)

  @tailrec
  private def askForGridSize(): Int =
    Dialog.showInput(null, "Enter grid size:", initial = "10") match
      case None => sys.exit(0)
      case Some(input)
          if input.toIntOption.isDefined && input.toInt >= minGridSize && input.toInt <= maxGridSize =>
        input.toInt
      case Some(_) =>
        Dialog.showMessage(
          null,
          s"Grid size must be a number between $minGridSize and $maxGridSize.",
          title = "Input Error",
          Dialog.Message.Error
        )
        askForGridSize()

  private def handleClick(pos: (Int, Int)): Unit =
    val selectedType = CellViewType.fromString(
      controlsPanel.soilTypeSelector.item
    ).getOrElse(CellViewType.Empty)
    if controlsPanel.drawLineButton.selected then
      handleDrawLine(pos, selectedType)
    else
      simController.handleViewMessage(PlaceCell(pos, selectedType))

  private def handleHover(pos: (Int, Int)): Unit =
    if controlsPanel.brushToggle.selected then
      val selectedType = CellViewType.fromString(
        controlsPanel.soilTypeSelector.item
      ).getOrElse(CellViewType.Empty)
      simController.handleViewMessage(PlaceCell(pos, selectedType))

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
          simController.handleViewMessage(PlaceLine(start, pos, cellViewType))
        }(ExecutionContext.global)
