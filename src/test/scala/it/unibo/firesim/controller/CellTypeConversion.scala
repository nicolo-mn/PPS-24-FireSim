package it.unibo.firesim.controller

import it.unibo.firesim.config.UIConfig.forestSoilStr
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CellTypeConversion extends AnyFlatSpec with Matchers:

  "CellViewType" should "be found with string constant" in {
    val forestCell = CellViewType.fromString(forestSoilStr)
    
    forestCell shouldBe Some(CellViewType.Forest)
  }