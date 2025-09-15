package it.unibo.firesim.model.map

import scala.util.Random

object MapBuilderDSL:

  def buildMap(
      rows: Int,
      cols: Int,
      random: Random = Random()
  )(instruction: MapBuilder ?=> Unit): Matrix =
    given builder: MapBuilder = MapBuilder(rows, cols, random)
    instruction(using builder)
    builder.build

  def withLakes(using builder: MapBuilder): Unit =
    builder.withLakes()

  def withForests(using builder: MapBuilder): Unit =
    builder.withForests()

  def withGrass(using builder: MapBuilder): Unit =
    builder.withGrass()

  def withStations(using builder: MapBuilder): Unit =
    builder.withStations()

  def withCustomTerrain(positions: Seq[((Int, Int), CellType)])(using
      builder: MapBuilder
  ): Unit =
    builder.withCustomTerrain(positions)
