package it.unibo.firesim.controller

import it.unibo.firesim.config.UIConfig.forestSoilStr

class CellTypeConversion extends AnyFlatSpec with Matchers:

  "CellViewType" should "be found with string constant" in {
    val forestCell = CellViewType.fromString(forestSoilStr)
    
    forestCell shouldBe Some(CellViewType.Forest)
  }