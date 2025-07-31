package it.unibo.firesim.controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}

import java.util.concurrent.atomic.AtomicInteger

class SimUpdaterSpec extends AnyFlatSpec with Matchers with Eventually:

  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(2, Seconds), interval = Span(50, Millis))

  "Updater" should "invoke callback periodically when running" in {
    val counter = new AtomicInteger(0)
    val updater: Updater = new SimUpdater(50) // tick ogni 50 ms
    updater.setUpdateCallback(() => counter.incrementAndGet())
    updater.start()

    // aspettiamo che almeno 3 invocazioni si verifichino
    eventually {
      counter.get() should be >= 3
    }

    updater.stop()
  }
  it should "not invoke callback when paused" in {
    val counter = new AtomicInteger(0)
    val updater = new SimUpdater(50)
    updater.setUpdateCallback(() => counter.incrementAndGet())
    updater.start()

    eventually {
      counter.get() should be >= 1
    }
    updater.pause()
    val beforePause = counter.get()

    Thread.sleep(200)
    counter.get() shouldEqual beforePause

    updater.stop()
  }
