package it.unibo.firesim.config

object Config:
  val fireFighterRay: Int = 3
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

  val minProbability = 0.0
  val maxProbability = 1.0

  // value for wind
  val grades = 360.0
  val halfSector: Double = grades / 2.0
  val windNormalization = 10.0
  val maxWindBoost = 1.0
  val baseWindBoost = 1.0
  val humidityPenalty = 0.7
  val highHumidity = 85

  // basic fire policy
  val humidityMidpoint = 70.0
  val humidityScale = 20.0
  val temperatureMidpoint = 20.0
  val temperatureScale = 5.0
  val neighborInfluenceWeight = 0.05
  val baseNeighborInfluence = 1.0

  // fire stage
  val ignitionProbabilityFactor = 0.3
  val ignitionActivationThreshold = 0.3
  val activeProbabilityFactor = 1.0
  val activeActivationThreshold = 0.8
  val smolderingProbabilityFactor = 0.2
  val smolderingActivationThreshold = 1.0

  // vegetation
  val forestFlammability = 0.02
  val grassFlammability = 0.05
  val noFlammability = 0.0
  val forestBurnDuration = 100
  val grassBurnDuration = 70
  val noBurnDuration = 0

  // water influence
  val coastalEffectMaxRange = 4
  val maxCoastalHumidityEffect = 0.5
