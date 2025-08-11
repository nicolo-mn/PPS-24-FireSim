package it.unibo.firesim.util

object Logger {

  def log(callerClass: Class[_], message: String): Unit =
    val millis = System.currentTimeMillis()
    val className = callerClass.getSimpleName
    println(f"[$millis][$className]: $message")

}