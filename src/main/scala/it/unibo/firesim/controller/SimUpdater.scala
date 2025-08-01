package it.unibo.firesim.controller

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

/** SimUpdater is a standalone scheduler that periodically invokes a
  * user-defined update callback. It is independent of any Model or View and
  * only manages the timing of updates.
  *
  * @param tickMs
  *   Interval between ticks in milliseconds
  */
class SimUpdater(tickMs: Long = 100) extends Updater with Runnable:
  @volatile private var running: Boolean = false
  @volatile private var paused: Boolean = false
  @volatile private var updateCallback: () => Unit = () => ()
  private var executor: ScheduledExecutorService = newExecutor()

  /** Register the callback to be invoked on each tick.
    *
    * @param cb
    *   Zero-argument function executed on every scheduler tick
    */
  override def setUpdateCallback(cb: () => Unit): Unit = synchronized {
    updateCallback = cb
  }

  private def newExecutor(): ScheduledExecutorService =
    Executors.newSingleThreadScheduledExecutor()

  /** Starts (or restarts) the periodic execution of the update callback. Has no
    * effect if already running. If previously stopped, a fresh executor is
    * created.
    */
  override def start(): Unit = synchronized {
    if running then return
    if executor.isShutdown then executor = newExecutor()

    running = true
    paused = false
    executor.scheduleAtFixedRate(this, 0, tickMs, TimeUnit.MILLISECONDS)
  }

  /** Stops the scheduler and prevents further callbacks. Once stopped, cannot
    * be restarted.
    */
  override def stop(): Unit = synchronized {
    if !running then return
    running = false
    executor.shutdown()
  }

  /** The Runnable run method invoked by the ScheduledExecutorService. It checks
    * the running and paused flags and executes the user-provided callback.
    */
  override def run(): Unit =
    if !running || paused then return
    try
      updateCallback()
    catch
      case ex: Exception =>
        // Log or handle exceptions from the callback as needed
        ex.printStackTrace()

  /** Pauses the execution of the update callback or Resumes execution of the
    * update callback after a pause.
    */
  override def pauseResume(): Unit = synchronized {
    if running && !paused then paused = true
    else paused = false
  }

  /** Checks whether the updater is currently running.
    */
  override def isRunning: Boolean = running

  /** Checks whether the updater is currently paused.
    */
  override def isPaused: Boolean = paused
