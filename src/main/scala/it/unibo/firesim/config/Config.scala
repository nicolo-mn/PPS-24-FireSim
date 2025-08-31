package it.unibo.firesim.config

object Config:
  val fireFighterRay: Int = 1
  val lakeGrowthProbability = 0.8
  val forestGrowthProbability = 0.7
  val grassGrowthProbability = 0.8

  // These ratios are intended to be multiplied by the mean between the number of rows and cols
  val lakeSeedFrequency = 0.01
  val forestSeedFrequency = 1.5
  val stationSeedsFrequency = 0.02

  val minLakeSizeRatio: Double = 1.5
  val maxLakeSizeRatio: Double = 3.0
  val minForestSizeRatio: Double = 0.3
  val maxForestSizeRatio: Double = 1.5
  val minGrassSizeRatio: Double = 0.1
  val maxGrassSizeRatio: Double = 0.4
