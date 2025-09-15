package it.unibo.firesim.model.firefighters

object FireFighterUtils:

  enum FireFighterAction:
    case Extinguish
    case Reload

  extension (f: FireFighter)

    def move: FireFighter =
      val (nextPos, strategy) = f.moveStrategy.move()
      f.copy(moveStrategy = strategy, position = nextPos)

    def changeTargetTo(target: (Int, Int)): FireFighter =
      f.copy(
        moveStrategy = f.moveStrategy.init(f.position, target),
        target = target
      )

    def when(cond: FireFighter => Boolean)(map: FireFighter => FireFighter)
        : FireFighter =
      if cond(f) then map(f) else f

    def action(fireCells: Set[(Int, Int)]): Option[FireFighterAction] =
      import FireFighterAction.*
      if f.loaded && fireCells.contains(f.position) && f.position == f.target
      then Option(Extinguish)
      else if !f.loaded && f.position == f.station then Option(Reload)
      else None
