package it.unibo.firesim.model.map

import scala.util.Random

trait MapGenerationStrategy:

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with water terrain
    */
  def addWater(matrix: Matrix, random: Random): Matrix

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with forest terrain
    */
  def addForests(matrix: Matrix, random: Random): Matrix

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with grass terrain
    */
  def addGrass(matrix: Matrix, random: Random): Matrix

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with stations
    */
  def addStations(matrix: Matrix, random: Random): Matrix

  /** @param matrix
    *   The Matrix to modify
    * @param random
    *   The initialized random class
    * @return
    *   the modified Matrix with fires
    */
  def addFires(matrix: Matrix, random: Random): Matrix

  /** @param matrix
    *   The Matrix to modify
    * @param row
    *   the row of the cell
    * @param col
    *   the column of the cell
    * @param cellType
    *   the type of cell
    * @return
    *   the modified Matrix with the custom terrain in the specified position
    */
  def addCustomTerrain(
      matrix: Matrix,
      row: Int,
      col: Int,
      cellType: CellType
  ): Matrix
