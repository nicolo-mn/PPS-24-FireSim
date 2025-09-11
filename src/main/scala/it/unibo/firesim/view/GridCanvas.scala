package it.unibo.firesim.view

import it.unibo.firesim.controller.CellViewType
import java.awt.Color
import scala.swing.{Graphics2D, Panel, Point, Swing}
import scala.swing.event.{MouseDragged, MousePressed}

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
