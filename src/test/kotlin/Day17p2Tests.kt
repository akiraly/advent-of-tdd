package day17p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day17p2Tests : FunSpec({
  val exampleHeatLossMapInput1 = """
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

  val exampleHeatLossMapInput2 = """
    111111111111
    999999999991
    999999999991
    999999999991
    999999999991
  """.trimIndent()

  test(""" Given the example heat loss map 1 then it can be parsed from text and turned back into text """) {
    val heatLossMap = exampleHeatLossMapInput1.toHeatLossMap()
    heatLossMap.numberOfRows shouldBe 13
    heatLossMap.numberOfColumns shouldBe 13
    heatLossMap.toText() shouldBe exampleHeatLossMapInput1
  }

  val customHeatLossMapInput = readInput("day17p1")

  test(""" Given the custom heat loss map then it can be parsed from text and turned back into text """) {
    val heatLossMap = customHeatLossMapInput.toHeatLossMap()
    heatLossMap.numberOfRows shouldBe 141
    heatLossMap.numberOfColumns shouldBe 141
    heatLossMap.toText() shouldBe customHeatLossMapInput
  }

  test(""" Given the example heat loss map 1 when the minimized heat loss path is calculated then the result is 94 """) {
    val heatLossMap = exampleHeatLossMapInput1.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 94
  }

  test(""" Given the example heat loss map 2 when the minimized heat loss path is calculated then the result is 71 """) {
    val heatLossMap = exampleHeatLossMapInput2.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 71
  }

  test(""" Given the custom heat loss map when the minimized heat loss path is calculated then the result is 1294 """) {
    val heatLossMap = customHeatLossMapInput.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 1294
  }
})
