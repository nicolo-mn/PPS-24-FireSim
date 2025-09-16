package it.unibo.firesim.model.map

import scala.util.Random

trait MapGenerationStrategy:

  def addLakes(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addForests(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addGrass(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addStations(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addFires(matrix: Matrix, rows: Int, cols: Int, random: Random): Matrix

  def addCustomTerrain(
      matrix: Matrix,
      row: Int,
      col: Int,
      cellType: CellType
  ): Matrix
