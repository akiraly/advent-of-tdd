package day21p2

import day21p2.Field.GardenPlot
import day21p2.Field.Rock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm

class Day21p2Tests : FunSpec({
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

    exampleMap.minDistanceToEdge shouldBe 6
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

  test(""" Given the example map when expanding 3 times it looks as expected""") {
    val exampleMapInputExpanded3 = exampleMapInput.expand(3)
    exampleMapInputExpanded3 shouldBe """
.................................
.....###.#......###.#......###.#.
.###.##..#..###.##..#..###.##..#.
..#.#...#....#.#...#....#.#...#..
....#.#........#.#........#.#....
.##...####..##...####..##...####.
.##..#...#..##..#...#..##..#...#.
.......##.........##.........##..
.##.#.####..##.#.####..##.#.####.
.##..##.##..##..##.##..##..##.##.
.................................
.................................
.....###.#......###.#......###.#.
.###.##..#..###.##..#..###.##..#.
..#.#...#....#.#...#....#.#...#..
....#.#........#.#........#.#....
.##...####..##..S####..##...####.
.##..#...#..##..#...#..##..#...#.
.......##.........##.........##..
.##.#.####..##.#.####..##.#.####.
.##..##.##..##..##.##..##..##.##.
.................................
.................................
.....###.#......###.#......###.#.
.###.##..#..###.##..#..###.##..#.
..#.#...#....#.#...#....#.#...#..
....#.#........#.#........#.#....
.##...####..##...####..##...####.
.##..#...#..##..#...#..##..#...#.
.......##.........##.........##..
.##.#.####..##.#.####..##.#.####.
.##..##.##..##..##.##..##..##.##.
.................................
    """.trimIndent()

    val exampleMap = exampleMapInput.expand(3).toGardenMap()
    exampleMap.distanceGridAsText() shouldBe """
  32   31   30   29   28   27   26   25   24   23   22   21   22   23   24   25   26   25   24   23   22   21   22   23   24   25   26   27   28   29   30   31   32
  31   32   31   30   29    #    #    #   25    #   21   20   21   22   23   24    #    #    #   24    #   20   21   22   23   24   25    #    #    #   29    #   31
  30    #    #    #   30    #    #   23   24    #   20   19    #    #    #   25    #    #   22   23    #   19   20    #    #    #   26    #    #   27   28    #   30
  29   28    #   26    #   24   23   22    #   20   19   18   19    #   21    #   23   22   21    #   19   18   19   20    #   22    #   24   25   26    #   30   29
  28   27   26   25    #   25    #   21   20   19   18   17   18   19   20    #   22    #   20   19   18   17   18   19   20   21    #   23    #   27   28   29   28
  27    #    #   24   25   26    #    #    #    #   17   16    #    #   19   20   21    #    #    #    #   16   17    #    #   20   21   22    #    #    #    #   27
  26    #    #   23   24    #   26   27   28    #   16   15    #    #   18   19    #   21   22   23    #   15   16    #    #   19   20    #   22   23   24    #   26
  25   24   23   22   23   24   25    #    #   16   15   14   15   16   17   18   19   20    #    #   15   14   15   16   17   18   19   20   21    #    #   26   25
  24    #    #   21    #   25    #    #    #    #   14   13    #    #   16    #   20    #    #    #    #   13   14    #    #   17    #   21    #    #    #    #   24
  23    #    #   20   19    #    #   16    #    #   13   12    #    #   15   14    #    #   11    #    #   12   13    #    #   16   17    #    #   20    #    #   23
  22   21   20   19   18   17   16   15   14   13   12   11   12   13   14   13   12   11   10    9   10   11   12   13   14   15   16   17   18   19   20   21   22
  21   20   19   18   17   16   15   14   13   12   11   10   11   12   13   12   11   10    9    8    9   10   11   12   13   14   15   16   17   18   19   20   21
  22   21   20   19   18    #    #    #   14    #   10    9   10   11   12   13    #    #    #    7    #   11   12   13   14   15   16    #    #    #   20    #   22
  23    #    #    #   19    #    #   12   13    #    9    8    #    #    #   14    #    #    5    6    #   10   11    #    #    #   17    #    #   20   21    #   23
  22   21    #   19    #   13   12   11    #    9    8    7    6    #    4    #    2    3    4    #    8    9   10   11    #   13    #   17   18   19    #   23   24
  21   20   19   18    #   14    #   10    9    8    7    6    5    4    3    #    1    #    5    6    7    8    9   10   11   12    #   16    #   20   21   22   23
  22    #    #   17   16   15    #    #    #    #    8    7    #    #    2    1    0    #    #    #    #    9   10    #    #   13   14   15    #    #    #    #   24
  23    #    #   18   17    #   21   22   23    #    9    8    #    #    3    2    #    6    7    8    #   10   11    #    #   14   15    #   19   20   21    #   25
  22   21   20   19   18   19   20    #    #    9    8    7    6    5    4    3    4    5    #    #   12   11   12   13   14   15   16   17   18    #    #   27   26
  23    #    #   20    #   20    #    #    #    #    9    8    #    #    5    #    5    #    #    #    #   12   13    #    #   16    #   18    #    #    #    #   27
  22    #    #   19   18    #    #   15    #    #   10    9    #    #    6    7    #    #   12    #    #   13   14    #    #   17   18    #    #   23    #    #   26
  21   20   19   18   17   16   15   14   13   12   11   10    9    8    7    8    9   10   11   12   13   14   15   16   17   18   19   20   21   22   23   24   25
  22   21   20   19   18   17   16   15   14   13   12   11   10    9    8    9   10   11   12   13   14   15   16   17   18   19   20   21   22   23   24   25   26
  23   22   21   20   19    #    #    #   15    #   13   12   11   10    9   10    #    #    #   14    #   16   17   18   19   20   21    #    #    #   25    #   27
  24    #    #    #   20    #    #   17   16    #   14   13    #    #    #   11    #    #   16   15    #   17   18    #    #    #   22    #    #   27   26    #   28
  25   26    #   26    #   20   19   18    #   16   15   14   15    #   19    #   19   18   17    #   19   18   19   20    #   24    #   28   29   28    #   30   29
  26   27   26   25    #   21    #   19   18   17   16   15   16   17   18    #   20    #   18   19   20   19   20   21   22   23    #   27    #   29   30   31   30
  27    #    #   24   23   22    #    #    #    #   17   16    #    #   19   20   21    #    #    #    #   20   21    #    #   24   25   26    #    #    #    #   31
  28    #    #   25   24    #   28   29   30    #   18   17    #    #   20   21    #   25   26   27    #   21   22    #    #   25   26    #   30   31   32    #   32
  29   28   27   26   25   26   27    #    #   20   19   18   19   20   21   22   23   24    #    #   23   22   23   24   25   26   27   28   29    #    #   34   33
  30    #    #   27    #   27    #    #    #    #   20   19    #    #   22    #   24    #    #    #    #   23   24    #    #   27    #   29    #    #    #    #   34
  31    #    #   28   29    #    #   26    #    #   21   20    #    #   23   24    #    #   29    #    #   24   25    #    #   28   29    #    #   34    #    #   35
  32   31   30   29   28   27   26   25   24   23   22   21   22   23   24   25   26   27   28   27   26   25   26   27   28   29   30   31   32   33   34   35   36
    """.lineSequence().filter { it.isNotBlank() }.joinToString("\n")

    exampleMap.minDistanceToEdge shouldBe 21
  }

  test(""" Given the custom map then the possible targets at (gridSize/2 + n * gridSize) steps can be calculated with a polynomial function """) {
    val baseMap = customMapInput.toGardenMap()

    val gridSize = baseMap.grid.size
    val halfGridSize = gridSize / 2

    gridSize shouldBe 131
    baseMap.grid.first().size shouldBe gridSize

    fun polynomialSteps(): Sequence<Int> {
      return generateSequence(0) { it + 1 }.map { halfGridSize + it * gridSize }
    }

    fun isPolynomialStep(n: Int): Boolean = (n - halfGridSize) % gridSize == 0

    val map = customMapInput.expand(21).toGardenMap()
    val intxs = polynomialSteps().take(3)

    val xs = intxs.map { it.toDouble() }.toList().toDoubleArray()
    val ys = intxs.map { map.numOfReachableGardenPlots(it).toDouble() }.toList().toDoubleArray()
    polynomialSteps().take(10).forEach { i ->
      isPolynomialStep(i) shouldBe true
      val num = map.numOfReachableGardenPlots(i)
      val interpolated = PolynomialFunctionLagrangeForm.evaluate(xs, ys, i.toDouble())
      interpolated shouldBe num
      println("$i\t$num\t$interpolated")
    }
    PolynomialFunctionLagrangeForm.evaluate(xs, ys, 26501365.0).toLong() shouldBe 1
  }
})

private fun String.expand(times: Int): String {
  if (times == 1) return this
  require(times % 2 == 1)
  val withoutS = replace("S", ".")
  val nonSRow = withoutS.lineSequence().map { it.repeat(times) }.joinToString("\n")
  val nonSHalfRow = withoutS.lineSequence().map { it.repeat(times / 2) }.joinToString("\n")
  val SRow = nonSHalfRow.lineSequence().zip(lineSequence()).map { (a, b) -> "$a$b$a" }.joinToString("\n")

  val nonSRowAllHalf = generateSequence { nonSRow }.take(times / 2).joinToString("\n")

  return nonSRowAllHalf + "\n" + SRow + "\n" + nonSRowAllHalf
}

fun String.numOfReachableGardenPlots(numOfSteps: Int): Int = toGardenMap().numOfReachableGardenPlots(numOfSteps)

fun String.toGardenMap(): GardenMap = GardenMap(
  grid = lineSequence().map { line ->
    line.map {
      when (it) {
        '.' -> GardenPlot
        'S' -> GardenPlot
        else -> {
          require(it == '#')
          Rock
        }
      }
    }
  }.toList(),
  start = lineSequence().flatMapIndexed { row, line ->
    line.mapIndexedNotNull { col, c ->
      if (c == 'S') Coordinate(row, col) else null
    }
  }.single()
)

data class GardenMap(val grid: List<List<Field>>, val start: Coordinate) {
  val distanceGrid = calcDistanceGrid()
  val minDistanceToEdge: Int = distanceGrid.calcMinDistanceToEdge()
  fun distanceGridAsText(): String = distanceGrid.joinToString("\n") {
    it.joinToString(" ") { n ->
      (n?.toString() ?: "#").padStart(4, ' ')
    }
  }

  private fun calcDistanceGrid(): List<List<Int?>> {
    val distanceGrid: List<MutableList<Int?>> = grid.map { it.indices.map { null }.toMutableList() }

    distanceGrid[start.row][start.col] = 0
    val toBeVisited = mutableListOf(start)
    while (toBeVisited.isNotEmpty()) {
      val current = toBeVisited.removeFirst()
      val distance = distanceGrid[current.row][current.col]!! + 1
      sequenceOf(
        current.copy(row = current.row - 1),
        current.copy(row = current.row + 1),
        current.copy(col = current.col - 1),
        current.copy(col = current.col + 1)
      ).filter { (row, col) -> row in grid.indices && col in grid[row].indices }
        .filter { (row, col) -> grid[row][col] == GardenPlot }
        .filter { (row, col) -> distanceGrid[row][col] == null || distanceGrid[row][col]!! > distance }
        .forEach { next ->
          distanceGrid[next.row][next.col] = distance
          toBeVisited.add(next)
        }
    }

    return distanceGrid
  }

  fun possibleTargetsGridAsText(numOfSteps: Int): String =
    distanceGrid.asSequence().withIndex().joinToString("\n") { (row, ints) ->
      ints.withIndex().joinToString("") { (col, n) ->
        if (n == null) "#" else {
          val diff = numOfSteps - n
          if (diff >= 0 && diff % 2 == 0) "O" else if (start == Coordinate(row, col)) "S" else "."
        }
      }
    }

  fun numOfReachableGardenPlots(numOfSteps: Int): Int =
    distanceGrid.asSequence().flatMap { ints ->
      ints.asSequence().filter { n ->
        if (n == null) false else {
          val diff = numOfSteps - n
          diff >= 0 && diff % 2 == 0
        }
      }
    }.count()
}

private fun List<List<Int?>>.calcMinDistanceToEdge(): Int = (
  first().asSequence() +
    last().asSequence() +
    asSequence().flatMap { sequenceOf(it.first(), it.last()) }
  )
  .filterNotNull().min()


enum class Field {
  GardenPlot,
  Rock
}

data class Coordinate(val row: Int, val col: Int)
