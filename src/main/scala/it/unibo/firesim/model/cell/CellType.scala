package it.unibo.firesim.model.cell

/** Enumeration of possible cell types in the simulation.
  *
  *   - `Forest`: a forest cell that can catch fire.
  *   - `Grass`: a grass cell that can (less likely) catch fire.
  *   - `Empty`: an empty cell that does not catch fire.
  *   - `Station`: a fire station cell that contains firefighters agents.
  *   - `Burning(startCycle: Int)`: the cell is burning, with `startCycle`
  *     indicating the cycle when it started burning.
  *   - `Burnt`: the cell has burnt out and is no longer active.
  */
enum CellType:
  case Forest, Grass, Empty, Station, Burnt
  case Burning(startCycle: Int)
