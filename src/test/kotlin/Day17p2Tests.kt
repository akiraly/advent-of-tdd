package day17p2

import day17p2.Direction.Down
import day17p2.Direction.Right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.readInput
import java.util.*

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

  test(""" Given the example heat loss map 1 then an initial path can be calculated """) {
    val heatLossMap = exampleHeatLossMapInput1.toHeatLossMap()
    val initialPath = heatLossMap.calculateInitialPath()
    initialPath.startRow shouldBe 0
    initialPath.startCol shouldBe 0
    initialPath.elements.first().totalHeatLoss shouldBeGreaterThan 0

    val lastEl = initialPath.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 133
  }

  test(""" Given the example heat loss map 1 when the minimized heat loss path is calculated then the result is 94 """) {
    val heatLossMap = exampleHeatLossMapInput1.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath2()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 94
  }

  test(""" Given the example heat loss map 2 when the minimized heat loss path is calculated then the result is 71 """) {
    val heatLossMap = exampleHeatLossMapInput2.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath2()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 71
  }

  test(""" Given the custom heat loss map when the minimized heat loss path is calculated then the result is 1294 """) {
    val heatLossMap = customHeatLossMapInput.toHeatLossMap()
    val path = heatLossMap.calculateMinHeatLossPath2()
    val lastEl = path.elements.last()

    lastEl.row shouldBe heatLossMap.numberOfRows - 1
    lastEl.col shouldBe heatLossMap.numberOfColumns - 1
    lastEl.totalHeatLoss shouldBe 1294
  }
})

fun String.toHeatLossMap(): HeatLossMap = HeatLossMap(
  lines().mapIndexed { row, line -> line.mapIndexed { col, ch -> Block(row, col, ch.digitToInt()) } }
)

data class HeatLossMap(val rows: List<List<Block>>) {
  val numberOfRows: Int = rows.size
  val numberOfColumns: Int = rows.first().size

  fun toText(): String = rows.joinToString("\n") { it.joinToString("") { box -> box.heatLoss.toString() } }
  fun calculateInitialPath(): Path {
    return minOf(
      calculateInitialPath(setOf(Right, Down)),
      calculateInitialPath(setOf(Down, Right)),
      compareBy { it.elements.last().totalHeatLoss }
    )
  }

  private fun calculateInitialPath(directions: Set<Direction>): Path {
    val pathEls = mutableListOf<PathEl>()

    for (direction in generateSequence { directions }.flatten().take(numberOfRows - 1 + numberOfColumns - 1)) {
      val pathEl = if (pathEls.isEmpty()) {
        val (nextRow, nextCol) = direction.next(0, 0)
        PathEl(
          direction,
          nextRow,
          nextCol,
          rows[nextRow][nextCol].heatLoss
        )
      } else {
        pathEls.last().next(direction) { row, col -> rows[row][col].heatLoss }
      }
      pathEls.add(pathEl)
    }

    return Path(elements = pathEls)
  }

  fun calculateMinHeatLossPath2(): Path2 {
    val grid: List<MutableList<PathEl2?>> = rows.map { row -> row.asSequence().map { null }.toMutableList() }
    val toVisit = TreeSet<PathEl2>()
    toVisit.add(PathEl2(Right, 0, 1, rows[0][1].heatLoss))
    toVisit.add(PathEl2(Down, 1, 0, rows[1][0].heatLoss))

    while (toVisit.isNotEmpty()) {
      val current = toVisit.removeFirst()
      if (grid[current.row][current.col] == null) {
        grid[current.row][current.col] = current
      }
      current.enterDirection.nextDirections().forEach { nextDirection ->
        val sameDirection = nextDirection == current.enterDirection
        if (!sameDirection && current.sameDirectionCount < 4) return@forEach
        val sameDirectionCount = if (sameDirection) current.sameDirectionCount + 1 else 1
        var previous = current
        val toVisits = mutableListOf<PathEl2>()
        var anyfree = false
        for (i in sameDirectionCount..10) {
          val (nextRow, nextCol) = nextDirection.next(previous.row, previous.col)
          if (!isValid(nextRow, nextCol)) {
            break
          }
          previous = PathEl2(
            nextDirection,
            nextRow,
            nextCol,
            previous.totalHeatLoss + rows[nextRow][nextCol].heatLoss,
            i,
            previous
          )
          toVisits.add(previous)
          if (grid[previous.row][previous.col] == null) {
            anyfree = true
          }
        }
        if (anyfree && toVisits.size + sameDirectionCount - 1 >= 4) toVisit.addAll(toVisits)
      }
    }

    var c = grid.last().last()
    val path = mutableListOf<PathEl2>()
    while (c != null) {
      path.addFirst(c)
      grid[c.row][c.col] = c
      c = c.previous
    }

    println(grid.joinToString("\n") { line ->
      line.joinToString(" ") {
        if (it == null) return@joinToString "".padStart(7, ' ')
        (it.enterDirection.sign.toString() +
          it.totalHeatLoss.toString() + "(" +
          rows[it.row][it.col].heatLoss + ")"
          ).padStart(7, ' ')
      }
    })

    println(path.joinToString(" | ") {
      (it.enterDirection.sign.toString() +
        it.totalHeatLoss.toString() + "(" +
        rows[it.row][it.col].heatLoss + ")"
        ).padStart(7, ' ')
    })

    return Path2(elements = path.toList())
  }

  class PathEl2(
    val enterDirection: Direction,
    val row: Int,
    val col: Int,
    val totalHeatLoss: Int,
    val sameDirectionCount: Int = 1,
    val previous: PathEl2? = null
  ) : Comparable<PathEl2> {

    override fun compareTo(other: PathEl2): Int = compareValuesBy(
      this, other,
      { it.totalHeatLoss },
      { it.row },
      { it.col },
      { it.enterDirection },
    )

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as PathEl2

      if (enterDirection != other.enterDirection) return false
      if (row != other.row) return false
      if (col != other.col) return false
      if (totalHeatLoss != other.totalHeatLoss) return false

      return true
    }

    override fun hashCode(): Int {
      var result = enterDirection.hashCode()
      result = 31 * result + row
      result = 31 * result + col
      result = 31 * result + totalHeatLoss
      return result
    }

    override fun toString(): String {
      return "PathEl2(enterDirection=$enterDirection, row=$row, col=$col, totalHeatLoss=$totalHeatLoss, sameDirectionCount=$sameDirectionCount, previous=$previous)"
    }
  }

  data class Path2(val elements: List<PathEl2>)

  fun calculateMinHeatLossPath(): Path {
    val calculator = MinHeatLossPathCalculator()
    calculator.calculateMinHeatLossPath()

    return calculator.currentBest
  }

  inner class MinHeatLossPathCalculator {
    var currentBest = calculateInitialPath()
    var maxHeatLoss = currentBest.elements.last().totalHeatLoss

    fun calculateMinHeatLossPath(
      path: Sequence<PathEl> = emptySequence(),
      seenPlaces: Set<Pair<Int, Int>> = emptySet(),
      row: Int = 0,
      col: Int = 0,
      previous: PathEl? = null
    ) {
      if (previous != null && previous.totalHeatLoss >= maxHeatLoss) {
        //println("end of the road... ${previous.totalHeatLoss} - $previous")
        return
      }

      if (row == numberOfRows - 1 && col == numberOfColumns - 1) {
        requireNotNull(previous)

        currentBest = Path(elements = path.toList())
        maxHeatLoss = previous.totalHeatLoss

        println("new best: $maxHeatLoss - $currentBest")

        return
      }

      val directions: MutableSet<Direction> = (previous?.enterDirection?.nextDirections() ?: setOf(Right, Down))
        .toMutableSet()

      if (previous?.singleDirectionCount == 3) {
        directions.remove(previous.enterDirection)
      }

      directions.forEach { direction ->
        val (nextRow, nextCol) = direction.next(row, col)
        if (!isValid(nextRow, nextCol) || seenPlaces.contains(Pair(nextRow, nextCol))) {
          return@forEach
        }

        val next = previous?.next(direction) { row, col -> rows[row][col].heatLoss }
          ?: PathEl(
            direction,
            nextRow,
            nextCol,
            rows[nextRow][nextCol].heatLoss
          )

        calculateMinHeatLossPath(
          path = path + next,
          seenPlaces = seenPlaces + Pair(nextRow, nextCol),
          row = nextRow,
          col = nextCol,
          previous = next
        )
      }
    }
  }

  private fun isValid(row: Int, col: Int): Boolean =
    row in 0 until numberOfRows && col in 0 until numberOfColumns
}

data class Block(val row: Int, val col: Int, val heatLoss: Int)

data class Path(val startRow: Int = 0, val startCol: Int = 0, val elements: List<PathEl>)

data class PathEl(
  val enterDirection: Direction,
  val row: Int,
  val col: Int,
  val totalHeatLoss: Int,
  val singleDirectionCount: Int = 1
) {
  init {
    require(singleDirectionCount <= 3)
  }

  fun next(direction: Direction, heatLossMapFn: (Int, Int) -> Int): PathEl {
    val (nextRow, nextCol) = direction.next(row, col)
    return PathEl(
      direction,
      nextRow,
      nextCol,
      totalHeatLoss + heatLossMapFn(nextRow, nextCol),
      if (enterDirection == direction) singleDirectionCount + 1 else 1
    )
  }
}

enum class Direction(val sign: Char) {
  Up('^') {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row - 1 to col

    override fun nextDirections(): Set<Direction> = setOf(Up, Left, Right)
  },
  Left('<') {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row to col - 1

    override fun nextDirections(): Set<Direction> = setOf(Left, Up, Down)
  },
  Down('v') {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row + 1 to col

    override fun nextDirections(): Set<Direction> = setOf(Down, Left, Right)
  },
  Right('>') {
    override fun next(row: Int, col: Int): Pair<Int, Int> = row to col + 1

    override fun nextDirections(): Set<Direction> = setOf(Right, Up, Down)
  };

  abstract fun next(row: Int, col: Int): Pair<Int, Int>

  abstract fun nextDirections(): Set<Direction>
}
