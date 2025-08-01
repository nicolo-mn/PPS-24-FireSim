package it.unibo.firesim.controller

/**
 * Updater is the interface for components responsible for managing the simulation's ticking mechanism.
 * It defines the basic control operations for the simulation lifecycle, such as starting, stopping,
 * and pausing/resuming periodic updates
 */
trait Updater:
  /**
   * Registers the callback to be invoked on each update tick.
   * @param cb Zero-argument function executed on every scheduler tick.
   */
  def setUpdateCallback(cb: () => Unit): Unit

  /**
   * Starts the simulation.
   */
  def start(): Unit

  /**
   * Stops periodic updates until a new simulation Start.
   */
  def stop(): Unit

  /**
   * Pauses or resumes periodic updates, depending on the current state.
   */
  def pauseResume(): Unit

  /**
   * Checks whether the updater is currently running.
   * @return true if the simulation is active, false if simulation is stopped.
   */
  def isRunning: Boolean

  /**
   * Checks whether the updater is currently paused.
   * @return true if simulation is not paused.
   */
  def isPaused: Boolean
