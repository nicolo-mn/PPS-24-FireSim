package it.unibo.firesim.model

type Matrix = Vector[Vector[CellType]]

extension (matrix: Matrix)
  /** Matrix rows
    * @return
    *   number of rows in the matrix
    */
  def rows: Int = matrix.length

  /** Matrix columns
    * @return
    *   number of columns in the matrix
    */
  def cols: Int = if matrix.nonEmpty then matrix.head.length else 0

  /** @param r
    *   Row index
    * @param c
    *   Column index
    * @param newCellType
    *   The new cellType to place at (r, c)
    * @return
    *   A new Matrix with the updated cell
    */
  def update(r: Int, c: Int, newCellType: CellType): Matrix =
    matrix.updated(r, matrix(r).updated(c, newCellType))

  /** @param r
    *   Row index
    * @param c
    *   Column index
    * @return
    *   True if the indices (r, c) are within the bounds of the matrix
    */
  def inBounds(r: Int, c: Int): Boolean =
    r >= 0 && r < matrix.length &&
      c >= 0 && c < matrix(r).length

  /** @param cellType
    *   The searched type of cells
    * @return
    *   The positions of the cells of that particular type
    */
  def positionsOf(cellType: CellType): Seq[(Int, Int)] =
    matrix.zipWithIndex.flatMap { case (row, i) =>
      row.zipWithIndex.collect {
        case (cT, j) if cT == cellType => (i, j)
      }
    }

  /** @param f
    *   The function that maps CellTypes into boolean
    * @return
    *   The positions of the cells with CellType mapped to true
    */
  def positionsOf(f: CellType => Boolean): Seq[(Int, Int)] =
    matrix.zipWithIndex.flatMap { case (row, i) =>
      row.zipWithIndex.collect {
        case (cT, j) if f(cT) => (i, j)
      }
    }
