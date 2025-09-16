package it.unibo.firesim.model.map

import CellType.*

import scala.util.Random

class MapBuilder(rows: Int, cols: Int, random: Random):
  private val mapGenerator: MapGenerationStrategy = MapGenerationWithRivers()
  private var matrix: Matrix = Vector.tabulate(rows, cols) { (r, c) => Rock }

  def withWater(): MapBuilder =
    matrix = mapGenerator.addWater(matrix, random)
    this

  def withForests(): MapBuilder =
    matrix = mapGenerator.addForests(matrix, random)
    this

  def withGrass(): MapBuilder =
    matrix = mapGenerator.addGrass(matrix, random)
    this

  def withStations(): MapBuilder =
    matrix = mapGenerator.addStations(matrix, random)
    this

  def withFires(): MapBuilder =
    matrix = mapGenerator.addFires(matrix, random)
    this

  def withCustomTerrain(positions: Seq[((Int, Int), CellType)]): MapBuilder =
    positions.foreach { case ((r, c), cellType) =>
      matrix = mapGenerator.addCustomTerrain(matrix, r, c, cellType)
    }
    this

  def build: Matrix = matrix
