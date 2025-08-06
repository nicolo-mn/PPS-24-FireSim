package it.unibo.firesim.controller

import it.unibo.firesim.config.UIConfig.forestSoilStr
import it.unibo.firesim.model.cell.CellType
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CellTypeConversion extends AnyFlatSpec with Matchers:

  "CellViewType" should "be found with string constant" in {
    val forestCell = CellViewType.fromString(forestSoilStr)

    forestCell shouldBe Some(CellViewType.Forest)
  }

  "CellTypeConverter" should "convert CellViewType to CellType correctly" in {
    val forestViewCell = CellViewType.Forest
    val convertedCellType = CellTypeConverter.toModel(forestViewCell)

    convertedCellType shouldBe CellType.Forest
  }

  "CellTypeConverter" should "convert CellType to CellViewType correctly" in {
    val forestCell = CellType.Forest
    val convertedCellViewType = CellTypeConverter.toView(forestCell)

    convertedCellViewType shouldBe CellViewType.Forest
  }