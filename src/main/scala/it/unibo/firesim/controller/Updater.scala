package it.unibo.firesim.controller

trait Updater:
  def setUpdateCallback(cb: () => Unit): Unit
  def start(): Unit
  def stop(): Unit
  def pauseResume(): Unit
  def isRunning: Boolean
  def isPaused: Boolean
