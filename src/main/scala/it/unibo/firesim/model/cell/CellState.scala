package it.unibo.firesim.model.cell

/** Enumeration of possible states of a cell in the fire simulation.
 *
 * - `Intact`: the cell is intact and not burning.
 *
 * - `Burning(startCycle: Int)`: the cell is burning, with `startCycle` indicating the cycle when it started burning.
 *
 * - `Burnt`: the cell has burnt out and is no longer active.
 *
 */
enum CellState:
  case Intact
  case Burning(startCycle: Int)
  case Burnt