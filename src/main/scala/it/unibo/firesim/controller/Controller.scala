package it.unibo.firesim.controller

trait Controller:

  /** Handles a message from the view by executing the corresponding action on
    * this controller.
    *
    * @param msg
    *   The message to process.
    */
  def handleViewMessage(msg: ViewMessage): Unit

  /** Main game loop. Once is called the main thread will stay in this loop
    * until the program is closed.
    *
    * @param tickMs
    *   The milliseconds every tick should last: could be more (simulation has
    *   delays) but not less.
    */
  def loop(tickMs: Int): Unit
