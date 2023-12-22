package day21p1

import day21p1.Field.GardenPlot
import day21p1.Field.Rock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day21p1Tests : FunSpec({
  val exampleMapInput = """
...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........
  """.trimIndent()

  val customMapInput = readInput("day21p1")

  context("parsing inputs") {
    test(""" Given the example map input then the map can be parsed as expected""") {
      val exampleMap = exampleMapInput.toGardenMap()
      exampleMap.start shouldBe Coordinate(5, 5)
      exampleMap.grid[5][5] shouldBe GardenPlot
      exampleMap.grid[5][6] shouldBe Rock
    }
  }

  test(""" Given the example map then the min number of steps to reach each garden plot are calculated correctly""") {
    val exampleMap = exampleMapInput.toGardenMap()
    exampleMap.distanceGridAsText() shouldBe """
  10   11   12   13   12   11   10    9    8    9   10
   9   10   11   12   13    #    #    #    7    #   11
   8    #    #    #   14    #    #    5    6    #   10
   7    6    #    4    #    2    3    4    #    8    9
   6    5    4    3    #    1    #    5    6    7    8
   7    #    #    2    1    0    #    #    #    #    9
   8    #    #    3    2    #    6    7    8    #   10
   7    6    5    4    3    4    5    #    #   12   11
   8    #    #    5    #    5    #    #    #    #   12
   9    #    #    6    7    #    #   12    #    #   13
  10    9    8    7    8    9   10   11   12   13   14
    """.lineSequence().filter { it.isNotBlank() }.joinToString("\n")
  }

  test(""" Given the example map then the possible targets at 1 step distance can be calculated correctly""") {
    val exampleMap = exampleMapInput.toGardenMap()

    exampleMap.possibleTargetsGridAsText(1) shouldBe """
...........
.....###.#.
.###.##..#.
..#.#...#..
....#O#....
.##.OS####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........
      """.trimIndent()
  }

  test(""" Given the example map then the possible targets at 2 steps distance can be calculated correctly""") {
    val exampleMap = exampleMapInput.toGardenMap()
    exampleMap.possibleTargetsGridAsText(2) shouldBe """
...........
.....###.#.
.###.##..#.
..#.#O..#..
....#.#....
.##O.O####.
.##.O#...#.
.......##..
.##.#.####.
.##..##.##.
...........
      """.trimIndent()
  }

  test(""" Given the example map then the possible targets at 3 steps distance can be calculated correctly""") {
    val exampleMap = exampleMapInput.toGardenMap()
    exampleMap.possibleTargetsGridAsText(3) shouldBe """
...........
.....###.#.
.###.##..#.
..#.#.O.#..
...O#O#....
.##.OS####.
.##O.#...#.
....O..##..
.##.#.####.
.##..##.##.
...........
      """.trimIndent()
  }

  test(""" Given the example map then the possible targets at 6 steps distance can be calculated correctly""") {
    val exampleMap = exampleMapInput.toGardenMap()
    exampleMap.possibleTargetsGridAsText(6) shouldBe """
...........
.....###.#.
.###.##.O#.
.O#O#O.O#..
O.O.#.#.O..
.##O.O####.
.##.O#O..#.
.O.O.O.##..
.##.#.####.
.##O.##.##.
...........
      """.trimIndent()
  }

  test(""" Given the example map when walking exactly 6 steps then the elf can reach 16 different garden plots""") {
    exampleMapInput.numOfReachableGardenPlots(6) shouldBe 16
  }

  test(""" Given the custom map when walking exactly 64 steps then the elf can reach 3503 different garden plots""") {
    customMapInput.numOfReachableGardenPlots(64) shouldBe 3503
  }
})
