//package it.unibo.firesim.controller
//
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.concurrent.Eventually
//import org.scalatest.time.{Millis, Seconds, Span}
//
//import java.util.concurrent.atomic.AtomicInteger
//
//class SimUpdaterSpec extends AnyFlatSpec with Matchers with Eventually:
//
//  implicit override val patienceConfig =
//    PatienceConfig(timeout = Span(2, Seconds), interval = Span(50, Millis))
//
//  "Updater" should "invoke callback periodically when running" in {
//    val counter = new AtomicInteger(0)
//    val updater: Updater = new SimUpdater(50) // tick ogni 50 ms
//    updater.setUpdateCallback(() => counter.incrementAndGet())
//    updater.start()
//
//    // aspettiamo che almeno 3 invocazioni si verifichino
//    eventually {
//      counter.get() should be >= 3
//    }
//
//    updater.stop()
//  }
//  it should "not invoke callback when paused" in {
//    val counter = new AtomicInteger(0)
//    val updater = new SimUpdater(50)
//    updater.setUpdateCallback(() => counter.incrementAndGet())
//    updater.start()
//
//    eventually {
//      counter.get() should be >= 1
//    }
//    updater.pauseResume()
//    val beforePause = counter.get()
//
//    Thread.sleep(200)
//    counter.get() shouldEqual beforePause
//
//    updater.stop()
//  }
//
//  it should "resume invoking callback after a pause" in {
//    val counter = new AtomicInteger(0)
//    val updater = new SimUpdater(60)
//    updater.setUpdateCallback(() => counter.incrementAndGet())
//    updater.start()
//
//    eventually {
//      counter.get() should be >= 2
//    }
//    updater.pauseResume()
//    val pausedCount = counter.get()
//
//    Thread.sleep(150)
//    counter.get() shouldEqual pausedCount
//
//    updater.pauseResume()
//
//    eventually {
//      counter.get() should be > pausedCount
//    }
//
//    updater.stop()
//  }
//
//  it should "report correct states via isRunning and isPaused" in {
//    val updater = new SimUpdater(100)
//    updater.isRunning shouldBe false
//    updater.isPaused shouldBe false
//
//    updater.start()
//    updater.isRunning shouldBe true
//    updater.isPaused shouldBe false
//
//    updater.pauseResume()
//    updater.isPaused shouldBe true
//
//    updater.pauseResume()
//    updater.isPaused shouldBe false
//
//    updater.stop()
//    updater.isRunning shouldBe false
//  }
