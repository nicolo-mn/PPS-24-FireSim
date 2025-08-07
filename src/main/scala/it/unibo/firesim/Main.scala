package it.unibo.firesim

import it.unibo.firesim.controller.{CellViewType, SimController}
import it.unibo.firesim.model.SimModel
import it.unibo.firesim.view.SimView

import scala.util.Random

object Main:

  def main(args: Array[String]): Unit =
    println("Hello, world")

//    val gui = new SimView(new SimController(new SimModel()))
//    val cellTypes = Seq(
//      CellViewType.Fire,
//      CellViewType.Empty,
//      CellViewType.Forest,
//      CellViewType.Grass
//    )
//    val viewMap = Seq.fill(100)(cellTypes(Random.nextInt(cellTypes.length)))
//    gui.setViewMap(viewMap)
//    javax.swing.SwingUtilities.invokeLater(() => ())
//    Thread.currentThread.join()

      val FPS = 60
      val tickMs = (1000.0 / FPS).toLong
      val controller = new SimController(new SimModel())
      controller.loop(tickMs)
