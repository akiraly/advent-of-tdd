package day18p1

import kotlin.math.absoluteValue

fun String.lavaCapacity(): Int {
  val digPlan = toDigPlan()
  val digGrid = digPlan.dig()
  digGrid.digInterior()
  return digGrid.capacity()
}

fun String.toDigPlan(): DigPlan = DigPlan(
  lines().map { it.toStep() }.toList()
)


private val stepRegex = """([RLUD]) (\d+) \((#\w+)\)""".toRegex()

fun String.toStep(): Step =
  stepRegex.matchEntire(this)!!.destructured
    .let { (direction, distance, colorCode) ->
      Step(
        direction = Direction.valueOf(direction),
        distance = distance.toInt(),
        colorCode = colorCode
      )
    }

data class DigPlan(val steps: List<Step>) {
  val lengthByDirection = steps.groupBy { it.direction }.mapValues { (_, steps) -> steps.sumOf { it.distance } }
  fun toText(): String = steps.joinToString("\n")
  fun length(): Int = steps.sumOf { it.distance }

  fun isLoop(): Boolean =
    lengthByDirection[Direction.R] == lengthByDirection[Direction.L] &&
      lengthByDirection[Direction.U] == lengthByDirection[Direction.D]

  fun rows(): Indexes {
    var max = 0
    var current = 0
    var min = 0
    for (step in steps) {
      when (step.direction) {
        Direction.D -> {
          current += step.distance
          max = max.coerceAtLeast(current)
        }

        Direction.U -> {
          current -= step.distance
          min = min.coerceAtMost(current)
        }

        else -> continue
      }
    }
    return Indexes(max, min)
  }

  fun columns(): Indexes {
    var max = 0
    var current = 0
    var min = 0
    for (step in steps) {
      when (step.direction) {
        Direction.R -> {
          current += step.distance
          max = max.coerceAtLeast(current)
        }

        Direction.L -> {
          current -= step.distance
          min = min.coerceAtMost(current)
        }

        else -> continue
      }
    }
    return Indexes(max, min)
  }

  data class Indexes(val max: Int, val min: Int) {
    val total: Int = max - min + 1
  }

  fun dig(): DigGrid {
    val rows = rows()
    val columns = columns()
    val digGrid = DigGrid((0 until rows.total).map { (0 until columns.total).map { Area.Terrain }.toMutableList() })
    var currentRow = rows.min.absoluteValue
    var currentCol = columns.min.absoluteValue
    digGrid.grid[currentRow][currentCol] = Area.Trench
    for (step in steps) {
      repeat(step.distance) {
        val (nextRow, nextColumn) = step.direction.next(currentRow, currentCol)
        require(nextRow >= 0) { "row $nextRow must be >= 0, $step / $it" }
        require(nextColumn >= 0) { "column $nextColumn must be >= 0, $step / $it" }
        digGrid.grid[nextRow][nextColumn] = Area.Trench
        currentRow = nextRow
        currentCol = nextColumn
      }
    }
    return digGrid
  }
}

data class DigGrid(val grid: List<MutableList<Area>>) {
  fun toText(): String = grid.joinToString("\n") { row -> row.joinToString("") { it.sign.toString() } }
  fun capacity(): Int = grid.asSequence().flatten().count { it == Area.Trench }

  fun digInterior() {
    val outsideAreas = mutableSetOf<Pair<Int, Int>>()
    for (row in grid.indices) {
      for (col in grid[row].indices) {
        if (grid[row][col] == Area.Trench || outsideAreas.contains(row to col)) {
          continue
        }
        outsideAreas.addAll(floodFill(row, col))
      }
    }
  }

  fun floodFill(startRow: Int, startCol: Int): Set<Pair<Int, Int>> {
    require(grid[startRow][startCol] == Area.Terrain)
    var inside = true
    val toVisit = mutableSetOf(startRow to startCol)
    val visited = mutableSetOf<Pair<Int, Int>>()
    while (toVisit.isNotEmpty()) {
      val (row, col) = toVisit.iterator().let {
        val pair = it.next()
        it.remove()
        pair
      }
      if (row == 0 || col == 0 || row == grid.size - 1 || col == grid[row].size - 1) {
        inside = false
      }
      visited.add(row to col)
      sequenceOf(
        row to col + 1,
        row + 1 to col,
        row to col - 1,
        row - 1 to col
      ).forEach { pair ->
        if (visited.contains(pair)) {
          return@forEach
        }
        val (r, c) = pair
        if (r < 0 || c < 0 || r >= grid.size || c >= grid[row].size) {
          return@forEach
        }
        if (grid[r][c] == Area.Trench) {
          return@forEach
        }

        toVisit.add(pair)
      }
    }

    if (inside) {
      visited.forEach { (row, col) -> grid[row][col] = Area.Trench }
      return emptySet()
    }

    return visited
  }
}

enum class Area(val sign: Char) {
  Trench('#'),
  Terrain('.')
}

data class Step(val direction: Direction, val distance: Int, val colorCode: String) {
  override fun toString(): String = "$direction $distance ($colorCode)"
}

enum class Direction {
  R {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row to col + 1

  },
  L {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row to col - 1

  },
  U {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row - 1 to col

  },
  D {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row + 1 to col
  };

  abstract fun next(row: Int, col: Int): Pair<Int, Int>
}
