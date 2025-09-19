package it.unibo.firesim.config

object Config:
  val fireFighterRay: Int = 3
  val distanceFromStationWeight: Double = 0.6
  val distanceFromPosWeight: Double = 0.4
  val correctionThreshold: Double = 0.9
  val lakeGrowthProbability: Double = 0.8
  val forestGrowthProbability: Double = 0.7
  val grassGrowthProbability: Double = 0.8

  // These ratios are intended to be multiplied by the mean between the number of rows and cols
  val lakeSeedFrequency: Double = 0.01
  val forestSeedFrequency: Double = 1.8
  val stationSeedsFrequency: Double = 0.02
  val forestFireSeedFrequency: Double = 0.01
  val grassFireSeedFrequency: Double = 0.01

  val minLakeSizeRatio: Double = 1.5
  val maxLakeSizeRatio: Double = 4.0
  val minForestSizeRatio: Double = 0.3
  val maxForestSizeRatio: Double = 1.5
  val minGrassSizeRatio: Double = 0.1
  val maxGrassSizeRatio: Double = 0.5

  val minProbability: Double = 0.0
  val maxProbability: Double = 1.0

  // value for wind
  val grades: Double = 360.0
  val halfSector: Double = grades / 2.0
  val windNormalization: Double = 25.0
  val maxWindBoost: Double = 1.0
  val baseWindBoost: Double = 1.0
  val humidityPenalty: Double = 0.7
  val highHumidity: Int = 85

  // basic fire policy
  val humidityMidpoint: Double = 70.0
  val humidityScale: Double = 20.0
  val temperatureMidpoint: Double = 20.0
  val temperatureScale: Double = 5.0
  val neighborInfluenceWeight: Double = 0.05
  val baseNeighborInfluence: Double = 1.0

  // fire stage
  val ignitionProbabilityFactor: Double = 0.3
  val ignitionActivationThreshold: Double = 0.3
  val activeProbabilityFactor: Double = 1.0
  val activeActivationThreshold: Double = 0.8
  val smolderingProbabilityFactor: Double = 0.2
  val smolderingActivationThreshold: Double = 1.0

  // vegetation
  val forestFlammability: Double = 0.02
  val grassFlammability: Double = 0.05
  val noFlammability: Double = 0.0
  val forestBurnDuration: Int = 100
  val grassBurnDuration: Int = 70
  val noBurnDuration: Int = 0

  // water influence
  val coastalEffectMaxRange: Int = 4
  val maxCoastalHumidityEffect: Double = 0.5
