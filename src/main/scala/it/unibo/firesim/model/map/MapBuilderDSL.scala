package it.unibo.firesim.model.map

import scala.util.Random

object MapBuilderDSL:

  /** @param rows
    *   the number of rows of the map
    * @param cols
    *   the number of columns of the map
    * @param random
    *   the initialized random class
    * @param instruction
    *   the building instruction to follow
    * @return
    *   the built matrix
    */
  def buildMap(
      rows: Int,
      cols: Int,
      random: Random = Random()
  )(instruction: MapBuilder ?=> Unit): Matrix =
    given builder: MapBuilder = MapBuilder(rows, cols, random)
    instruction(using builder)
    builder.build

  /** Instruction to build map with water
    * @param builder
    *   used to build the map
    */
  def withWater(using builder: MapBuilder): Unit =
    builder.withWater()

  /** Instruction to build map with forests
    * @param builder
    *   used to build the map
    */
  def withForests(using builder: MapBuilder): Unit =
    builder.withForests()

  /** Instruction to build map with grass
    * @param builder
    *   used to build the map
    */
  def withGrass(using builder: MapBuilder): Unit =
    builder.withGrass()

  /** Instruction to build map with stations
    * @param builder
    *   used to build the map
    */
  def withStations(using builder: MapBuilder): Unit =
    builder.withStations()

  /** Instruction to build map with fires
    * @param builder
    *   used to build the map
    */
  def withFires(using builder: MapBuilder): Unit =
    builder.withFires()

  /** Instruction to build map with custom terrain
    * @param builder
    *   used to build the map
    */
  def withCustomTerrain(positions: Seq[((Int, Int), CellType)])(using
      builder: MapBuilder
  ): Unit =
    builder.withCustomTerrain(positions)
