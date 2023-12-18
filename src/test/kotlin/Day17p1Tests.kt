package day17p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day17p1Tests : FunSpec({
  val exampleHeatLossMapInput = """
    2413432311323
    3215453535623
    3255245654254
    3446585845452
    4546657867536
    1438598798454
    4457876987766
    3637877979653
    4654967986887
    4564679986453
    1224686865563
    2546548887735
    4322674655533
  """.trimIndent()

  test(""" Given the example heat loss map then it can be parsed from text and turned back into text """) {
    val heatLossMap = exampleHeatLossMapInput.toHeatLossMap()
    heatLossMap.numberOfRows shouldBe 13
    heatLossMap.numberOfColumns shouldBe 13
    heatLossMap.toText() shouldBe exampleHeatLossMapInput
  }

  val customHeatLossMapInput = readInput("day17p1")

  test(""" Given the custom heat loss map then it can be parsed from text and turned back into text """) {
    val heatLossMap = customHeatLossMapInput.toHeatLossMap()
    heatLossMap.numberOfRows shouldBe 141
    heatLossMap.numberOfColumns shouldBe 141
    heatLossMap.toText() shouldBe customHeatLossMapInput
  }

  test(""" Given the example heat loss map when the minimized heat loss path is calculated then the result is 102 """) {
    val heatLossMap = exampleHeatLossMapInput.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 102
  }

  test(""" Given the custom heat loss map when the minimized heat loss path is calculated then the result is 1110 """) {
    val heatLossMap = customHeatLossMapInput.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 1110
  }
})
