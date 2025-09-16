package it.unibo.firesim.model.map

import scala.util.Random

trait MapGenerationStrategy:

  protected def roundedMeanMul(ratio: Double, rows: Int, cols: Int): Int =
    (ratio * (rows + cols) / 2).round.toInt

  def addWater(matrix: Matrix, random: Random): Matrix

  def addForests(matrix: Matrix, random: Random): Matrix

  def addGrass(matrix: Matrix, random: Random): Matrix

  def addStations(matrix: Matrix, random: Random): Matrix

  def addFires(matrix: Matrix, random: Random): Matrix

  def addCustomTerrain(
      matrix: Matrix,
      row: Int,
      col: Int,
      cellType: CellType
  ): Matrix
