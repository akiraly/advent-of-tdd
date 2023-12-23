package day21p2

import day21p2.Field.GardenPlot
import day21p2.Field.Rock

fun String.expand(times: Int): String {
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

  val gridSize = grid.size
  private val halfGridSize = gridSize / 2

  fun polynomialSteps(): Sequence<Int> {
    require(gridSize == grid.first().size)
    return generateSequence(0) { it + 1 }.map { halfGridSize + it * gridSize }
  }

  fun isPolynomialStep(n: Int): Boolean = (n - halfGridSize) % gridSize == 0
}


enum class Field {
  GardenPlot,
  Rock
}

data class Coordinate(val row: Int, val col: Int)
