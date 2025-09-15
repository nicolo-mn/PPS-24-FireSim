package it.unibo.firesim.model.map

import CellType.*

import scala.util.Random

class MapBuilder(rows: Int, cols: Int, random: Random):
  private var matrix: Matrix = Vector.tabulate(rows, cols) { (r, c) => Rock }

  def withLakes(): MapBuilder =
    matrix = MapGenerator.addLakes(matrix, rows, cols, random)
    this

  def withForests(): MapBuilder =
    matrix = MapGenerator.addForests(matrix, rows, cols, random)
    this

  def withGrass(): MapBuilder =
    matrix = MapGenerator.addGrass(matrix, rows, cols, random)
    this

  def withStations(): MapBuilder =
    matrix = MapGenerator.addStations(matrix, rows, cols, random)
    this

  def withCustomTerrain(positions: Seq[((Int, Int), CellType)]): MapBuilder =
    positions.foreach { case ((r, c), cellType) =>
      matrix = MapGenerator.addCustomTerrain(matrix, r, c, cellType)
    }
    this

  def build: Matrix = matrix
