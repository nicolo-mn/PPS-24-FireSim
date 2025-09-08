package it.unibo.firesim

import it.unibo.firesim.controller.SimController
import it.unibo.firesim.model.SimModel

object Main:

  def main(args: Array[String]): Unit =

    val TPS = 30
    val tickMs = (1000.0 / TPS).toInt
    val controller = new SimController(new SimModel())
    controller.loop(tickMs)
