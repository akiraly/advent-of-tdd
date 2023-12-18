package day17p1

import day17p1.Direction.Down
import day17p1.Direction.Right
import java.util.*

fun String.toHeatLossMap(): HeatLossMap = HeatLossMap(
  lines().mapIndexed { row, line -> line.mapIndexed { col, ch -> Block(row, col, ch.digitToInt()) } }
)

data class HeatLossMap(val rows: List<List<Block>>) {
  val numberOfRows: Int = rows.size
  val numberOfColumns: Int = rows.first().size

  init {
    require(numberOfRows == numberOfColumns)
  }

  fun toText(): String = rows.joinToString("\n") { it.joinToString("") { box -> box.heatLoss.toString() } }

  fun calculateMinHeatLossPath(): Path {
    val grid: List<MutableList<PathEl?>> = rows.map { row -> row.asSequence().map { null }.toMutableList() }
    val toVisit = TreeSet<PathEl>()
    toVisit.add(PathEl(Right, 0, 1, rows[0][1].heatLoss))
    toVisit.add(PathEl(Down, 1, 0, rows[1][0].heatLoss))

    while (toVisit.isNotEmpty()) {
      val current = toVisit.removeFirst()
      if (grid[current.row][current.col] == null) {
        grid[current.row][current.col] = current
      }
      current.enterDirection.nextDirections().forEach { nextDirection ->
        val sameDirection = nextDirection == current.enterDirection
        val sameDirectionCount = if (sameDirection) current.sameDirectionCount + 1 else 1
        var previous = current
        val toVisits = mutableListOf<PathEl>()
        var anyfree = false
        for (i in sameDirectionCount..3) {
          val (nextRow, nextCol) = nextDirection.next(previous.row, previous.col)
          if (!isValid(nextRow, nextCol)) {
            break
          }
          previous = PathEl(
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
        if (anyfree) toVisit.addAll(toVisits)
      }
    }

    var c = grid.last().last()
    val path = mutableListOf<PathEl>()
    while (c != null) {
      path.addFirst(c)
      grid[c.row][c.col] = c
      c = c.previous
    }

    return Path(elements = path.toList())
  }

  class PathEl(
    val enterDirection: Direction,
    val row: Int,
    val col: Int,
    val totalHeatLoss: Int,
    val sameDirectionCount: Int = 1,
    val previous: PathEl? = null
  ) : Comparable<PathEl> {

    override fun compareTo(other: PathEl): Int = compareValuesBy(
      this, other,
      { it.totalHeatLoss },
      { it.row },
      { it.col },
      { it.enterDirection },
    )

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as PathEl

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
      return "PathEl(enterDirection=${enterDirection.sign}, row=$row, col=$col, totalHeatLoss=$totalHeatLoss, sameDirectionCount=$sameDirectionCount, previous=$previous)"
    }
  }

  data class Path(val elements: List<PathEl>)

  private fun isValid(row: Int, col: Int): Boolean =
    row in 0 until numberOfRows && col in 0 until numberOfColumns
}

data class Block(val row: Int, val col: Int, val heatLoss: Int)

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

