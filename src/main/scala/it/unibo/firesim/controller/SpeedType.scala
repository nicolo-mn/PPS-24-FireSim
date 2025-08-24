package it.unibo.firesim.controller

enum SpeedType(val id: String, val multiplier: Double):
  case Speed0_5x extends SpeedType("0.5x", 0.5)
  case Speed1x   extends SpeedType("1x", 1)
  case Speed1_5x extends SpeedType("1.5x", 1.5)
  case Speed2x   extends SpeedType("2x", 2)
  case Speed3x   extends SpeedType("3x", 3)