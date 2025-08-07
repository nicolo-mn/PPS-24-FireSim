package it.unibo.firesim.model

object FireFighter:

  def apply(rows: Int, cols: Int, station: (Int, Int)): FireFighter =
    new FireFighter(rows, cols, station)

case class FireFighterUpdate(
    position: (Int, Int),
    extinguishedCells: Seq[(Int, Int)]
)

class FireFighter(
    private val rows: Int,
    private val cols: Int,
    private val station: (Int, Int)
):

  def act(burningCells: Seq[(Int, Int)]): FireFighterUpdate =
    FireFighterUpdate((0, 0), Seq())
