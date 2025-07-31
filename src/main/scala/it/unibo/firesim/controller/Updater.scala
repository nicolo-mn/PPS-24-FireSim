package it.unibo.firesim.controller

trait Updater {
  def setUpdateCallback(cb: () => Unit): Unit
  def start(): Unit
  def stop(): Unit
  def pause(): Unit
  def resume(): Unit
  def isRunning: Boolean
  def isPaused: Boolean
}
