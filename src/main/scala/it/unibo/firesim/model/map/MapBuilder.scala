package it.unibo.firesim.model.map

import CellType.*

import scala.util.Random

class MapBuilder(rows: Int, cols: Int, random: Random):
  private val mapGenerator: MapGenerationStrategy = MapGenerationWithRivers()
  private var matrix: Matrix = Vector.tabulate(rows, cols) { (r, c) => Rock }

  /** Adds water generation to the map
    * @return
    *   the builder
    */
  def withWater(): MapBuilder =
    matrix = mapGenerator.addWater(matrix, random)
    this

  /** Adds forest generation to the map
    * @return
    *   the builder
    */
  def withForests(): MapBuilder =
    matrix = mapGenerator.addForests(matrix, random)
    this

  /** Adds grass generation to the map
    * @return
    *   the builder
    */
  def withGrass(): MapBuilder =
    matrix = mapGenerator.addGrass(matrix, random)
    this

  /** Adds station placement to the map
    * @return
    *   the builder
    */
  def withStations(): MapBuilder =
    matrix = mapGenerator.addStations(matrix, random)
    this

  /** Adds station placement to the map
    * @return
    *   the builder
    */
  def withFires(): MapBuilder =
    matrix = mapGenerator.addFires(matrix, random)
    this

  /** Adds custom terrains to the map
    * @param positions
    *   The positions and type of cells to add
    * @return
    *   the builder
    */
  def withCustomTerrain(positions: Seq[(Position, CellType)]): MapBuilder =
    positions.foreach { case ((r, c), cellType) =>
      matrix = mapGenerator.addCustomTerrain(matrix, r, c, cellType)
    }
    this

  /** @return
    *   the built map
    */
  def build: Matrix = matrix
